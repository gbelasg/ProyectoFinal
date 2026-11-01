package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.exceptions.MontoInvalidoException;
import co.edu.uniquindio.poo.exceptions.SaldoInsuficienteException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Deposito;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.MonederoVirtual;
import co.edu.uniquindio.poo.model.Retiro;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.Transferencia;
import co.edu.uniquindio.poo.util.Fecha;
import co.edu.uniquindio.poo.util.Formato;

import java.util.ArrayList;

// Depositos, retiros, transferencias y reversiones
public class TransaccionController {

    private MonederoVirtual modelo;
    private MonederoController monederoController;
    private PuntosController puntosController;
    private AnalisisController analisisController;

    public TransaccionController(MonederoVirtual modelo, MonederoController monederoController,
                                 PuntosController puntosController, AnalisisController analisisController) {
        this.modelo = modelo;
        this.monederoController = monederoController;
        this.puntosController = puntosController;
        this.analisisController = analisisController;
    }

    public Transaccion depositar(Cliente cliente, Monedero destino, double monto)
            throws MontoInvalidoException, SaldoInsuficienteException {
        validarMonto(monto);
        monederoController.validarPropietario(cliente, destino);
        Deposito deposito = new Deposito(modelo.generarCodigoTransaccion(), Fecha.hoy(), monto, destino);
        ejecutarTransaccion(cliente, deposito);
        return deposito;
    }

    public Transaccion retirar(Cliente cliente, Monedero origen, double monto)
            throws MontoInvalidoException, SaldoInsuficienteException {
        validarMonto(monto);
        monederoController.validarPropietario(cliente, origen);
        Retiro retiro = new Retiro(modelo.generarCodigoTransaccion(), Fecha.hoy(), monto, origen);
        ejecutarTransaccion(cliente, retiro);
        return retiro;
    }

    // El origen debe ser del emisor y el destino del receptor
    public Transaccion transferir(Cliente emisor, Monedero origen, Cliente receptor, Monedero destino, double monto)
            throws MontoInvalidoException, SaldoInsuficienteException {
        validarMonto(monto);
        if (receptor == null) {
            throw new IllegalArgumentException("Seleccione el cliente que recibe la transferencia.");
        }
        monederoController.validarPropietario(emisor, origen);
        monederoController.validarPropietario(receptor, destino);
        if (origen == destino) {
            throw new IllegalArgumentException("El monedero de origen y el de destino no pueden ser el mismo.");
        }
        Transferencia transferencia = new Transferencia(modelo.generarCodigoTransaccion(), Fecha.hoy(), monto, origen, destino);
        ejecutarTransaccion(emisor, transferencia);
        if (receptor != emisor) {
            receptor.notificar("Recibiste " + Formato.dinero(monto) + " de " + emisor.getNombre()
                    + " en tu monedero " + destino.getNombre() + ".");
        }
        return transferencia;
    }

    public void revertirTransaccion(Cliente cliente, String codigo) throws SaldoInsuficienteException {
        if (cliente == null) {
            throw new IllegalArgumentException("Seleccione primero un cliente.");
        }
        Transaccion transaccion = cliente.consultarHistorial().stream()
                .filter(t -> t.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()
                .orElse(null);
        if (transaccion == null) {
            throw new IllegalArgumentException("No existe la transaccion " + codigo + " en el historial del cliente.");
        } else if (transaccion.isRevertida()) {
            throw new IllegalArgumentException("La transaccion " + codigo + " ya fue revertida.");
        } else if (!cliente.realizoTransaccion(transaccion)) {
            throw new IllegalArgumentException("Solo el cliente que hizo la transaccion puede revertirla.");
        }
        transaccion.revertir();
        cliente.recalcularPuntos();
        puntosController.actualizarRango(cliente);
        cliente.notificar("Se revirtio la transaccion " + codigo + ". Tus puntos ahora son " + cliente.getPuntosAcumulados() + ".");
    }

    // Historial del cliente, con filtro por tipo ("Todos", "Deposito", "Retiro" o "Transferencia")
    public ArrayList<Transaccion> consultarHistorial(Cliente cliente, String tipo) {
        return new ArrayList<>(cliente.consultarHistorial().stream()
                .filter(t -> tipo == null || tipo.equals("Todos") || t.obtenerTipo().equals(tipo))
                .toList());
    }

    // Camino comun de todas las transacciones (normales y programadas)
    public void ejecutarTransaccion(Cliente cliente, Transaccion transaccion) throws SaldoInsuficienteException {
        // La comision se redondea a 2 decimales
        transaccion.setComision(Math.round(transaccion.calcularComision(cliente) * 100) / 100.0);
        transaccion.ejecutar();
        procesarTransaccion(cliente, transaccion);
    }

    // Despues de cada transaccion: atipica, puntos, rango y saldo bajo
    private void procesarTransaccion(Cliente cliente, Transaccion transaccion) {
        ArrayList<Transaccion> historialAnterior = new ArrayList<>(cliente.consultarHistorial().stream()
                .filter(t -> t != transaccion && !t.isRevertida())
                .toList());
        if (analisisController.esAtipica(transaccion, historialAnterior)) {
            transaccion.marcarAtipica();
            cliente.notificar("ALERTA: la transaccion " + transaccion.getCodigo() + " por " + Formato.dinero(transaccion.getMonto())
                    + " es atipica (supera promedio + 2 x desviacion). Revisa si la reconoces.");
        }

        puntosController.registrarPuntos(cliente, transaccion);

        cliente.getListaMonederos().stream()
                .filter(Monedero::tieneSaldoBajo)
                .forEach(m -> cliente.notificar("Saldo bajo en el monedero " + m.getNombre() + ": "
                        + Formato.dinero(m.getSaldo()) + " (minimo " + Formato.dinero(m.getSaldoMinimoAlerta()) + ")."));
    }

    public void validarMonto(double monto) throws MontoInvalidoException {
        if (monto <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor que 0.");
        }
    }
}
