package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.exceptions.MontoInvalidoException;
import co.edu.uniquindio.poo.exceptions.SaldoInsuficienteException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.MonederoVirtual;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.TransaccionProgramada;
import co.edu.uniquindio.poo.util.Fecha;
import co.edu.uniquindio.poo.util.Formato;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class ProgramadaController {

    private MonederoVirtual modelo;
    private MonederoController monederoController;
    private TransaccionController transaccionController;

    public ProgramadaController(MonederoVirtual modelo, MonederoController monederoController,
                                TransaccionController transaccionController) {
        this.modelo = modelo;
        this.monederoController = monederoController;
        this.transaccionController = transaccionController;
    }

    public TransaccionProgramada programarTransaccion(Cliente cliente, String tipo, Monedero origen, Monedero destino,
                                                      double monto, String fecha, int frecuenciaMeses)
            throws MontoInvalidoException {
        transaccionController.validarMonto(monto);
        if (tipo == null) {
            throw new IllegalArgumentException("Seleccione el tipo de transaccion.");
        }
        validarFechaYFrecuencia(fecha, frecuenciaMeses);
        if (destino == null) {
            throw new IllegalArgumentException("Seleccione el monedero destino.");
        }
        Monedero origenFinal = null;
        if (tipo.equals("Deposito")) {
            monederoController.validarPropietario(cliente, destino);
        } else {
            monederoController.validarPropietario(cliente, origen);
            if (origen == destino) {
                throw new IllegalArgumentException("El monedero de origen y el de destino no pueden ser el mismo.");
            }
            origenFinal = origen;
        }
        TransaccionProgramada programada = new TransaccionProgramada(modelo.generarCodigoProgramada(), tipo, monto, fecha,
                frecuenciaMeses, cliente, origenFinal, destino);
        modelo.getListaProgramadas().add(programada);
        cliente.notificar("Programaste un(a) " + tipo + " de " + Formato.dinero(monto) + " para el " + fecha
                + ", cada " + frecuenciaMeses + " mes(es).");
        return programada;
    }

    public void actualizarProgramada(Cliente cliente, TransaccionProgramada programada, double monto,
                                     String fecha, int frecuenciaMeses) throws MontoInvalidoException {
        validarProgramadaDelCliente(cliente, programada);
        transaccionController.validarMonto(monto);
        validarFechaYFrecuencia(fecha, frecuenciaMeses);
        programada.modificar(monto, String.valueOf(LocalDateTime.parse(fecha)), frecuenciaMeses);
        cliente.notificar("Modificaste la transaccion programada " + programada.getCodigo() + ": "
                + Formato.dinero(monto) + " el " + fecha + ", cada " + frecuenciaMeses + " mes(es).");
    }

    public void eliminarProgramada(Cliente cliente, TransaccionProgramada programada) {
        validarProgramadaDelCliente(cliente, programada);
        modelo.getListaProgramadas().remove(programada);
        cliente.notificar("Eliminaste la transaccion programada " + programada.getCodigo() + ".");
    }


    public void ordenarProgramadas() {
        ArrayList<TransaccionProgramada> lista = modelo.getListaProgramadas();
        for (int i = 1; i < lista.size(); i++) {
            TransaccionProgramada actual = lista.get(i);
            int j = i - 1;
            while (j >= 0 && lista.get(j).getFechaEjecucion().compareTo(actual.getFechaEjecucion()) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, actual);
        }
    }


    public String procesarProgramadas(String fechaActual) {
        if (!Fecha.esValida(fechaActual)) {
            throw new IllegalArgumentException("La fecha de hoy debe ser valida y tener el formato aaaa-mm-dd.");
        }
        ordenarProgramadas();
        String resumen = "";
        int ejecutadas = 0;
        int fallidas = 0;
        for (TransaccionProgramada p : modelo.getListaProgramadas()) {
            boolean seguir = true;
            while (seguir && p.estaPendiente(fechaActual)) {
                Transaccion transaccion = p.crearTransaccion(modelo.generarCodigoTransaccion());
                try {
                    transaccionController.ejecutarTransaccion(p.getCliente(), transaccion);
                    resumen += "OK  " + p.getCodigo() + " " + p.getTipo() + " de " + Formato.dinero(p.getMonto())
                            + " (" + p.getFechaEjecucion() + ") -> " + transaccion.getCodigo() + "\n";
                    p.getCliente().notificar("Se ejecuto la transaccion programada " + p.getCodigo() + " por "
                            + Formato.dinero(p.getMonto()) + ".");
                    p.avanzarFecha();
                    ejecutadas++;
                } catch (SaldoInsuficienteException e) {
                    resumen += "FALLO " + p.getCodigo() + ": " + e.getMessage() + "\n";
                    p.getCliente().notificar("No se pudo ejecutar la transaccion programada " + p.getCodigo()
                            + ": " + e.getMessage());
                    fallidas++;
                    seguir = false;
                }
            }
        }

        String mesActual = fechaActual.substring(0, 7);
        for (TransaccionProgramada p : modelo.getListaProgramadas()) {
            String fecha = p.getFechaEjecucion();
            if (p.isActiva() && fecha.compareTo(fechaActual) > 0 && fecha.startsWith(mesActual)) {
                resumen += "RECORDATORIO " + p.getCodigo() + ": " + p.getTipo() + " de " + Formato.dinero(p.getMonto())
                        + " el " + fecha + "\n";
                p.getCliente().notificar("Recordatorio: tu transaccion programada " + p.getCodigo() + " de "
                        + Formato.dinero(p.getMonto()) + " se ejecutara el " + fecha + ".");
            }
        }
        return "Programadas ejecutadas: " + ejecutadas + "   Fallidas: " + fallidas + "\n\n" + resumen;
    }

    public ArrayList<TransaccionProgramada> obtenerProgramadasDe(Cliente cliente) {
        return new ArrayList<>(modelo.getListaProgramadas().stream()
                .filter(p -> p.getCliente() == cliente)
                .toList());
    }

    private void validarFechaYFrecuencia(String fecha, int frecuenciaMeses) {
        if (!Fecha.esValida(fecha)) {
            throw new IllegalArgumentException("La fecha debe ser valida y tener el formato aaaa-mm-dd.");
        }
        if (frecuenciaMeses < 1) {
            throw new IllegalArgumentException("La frecuencia debe ser de al menos 1 mes.");
        }
    }

    private void validarProgramadaDelCliente(Cliente cliente, TransaccionProgramada programada) {
        if (cliente == null || programada == null) {
            throw new IllegalArgumentException("Seleccione la transaccion programada en la tabla.");
        }
        if (programada.getCliente() != cliente) {
            throw new IllegalArgumentException("La transaccion programada no pertenece al cliente activo.");
        }
    }

    public ArrayList<TransaccionProgramada> getListaProgramadas() {
        return modelo.getListaProgramadas();
    }
}
