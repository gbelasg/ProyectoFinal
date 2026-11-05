package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.exceptions.PuntosInsuficientesException;
import co.edu.uniquindio.poo.model.Beneficio;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.MonederoVirtual;
import co.edu.uniquindio.poo.model.Rango;
import co.edu.uniquindio.poo.model.RegistroPuntos;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.util.Fecha;
import co.edu.uniquindio.poo.util.Formato;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PuntosController {

    private MonederoVirtual modelo;

    public PuntosController(MonederoVirtual modelo) {
        this.modelo = modelo;
    }


    public Rango calcularRango(int puntos) {
        return modelo.getListaRangos().stream()
                .filter(r -> r.contienePuntos(puntos))
                .findFirst()
                .orElse(modelo.getListaRangos().get(0));
    }


    public void actualizarRango(Cliente cliente) {
        Rango nuevo = calcularRango(cliente.getPuntosAcumulados());
        if (nuevo != cliente.getRango()) {
            cliente.setRango(nuevo);
            cliente.notificar("Tu rango ahora es " + nuevo.getNombre() + ".");
        }
    }


    public void registrarPuntos(Cliente cliente, Transaccion transaccion) {
        int puntosBase = transaccion.calcularPuntos();
        int bono = (int) (puntosBase * cliente.getRango().getBonoPuntos());
        transaccion.setPuntosGenerados(puntosBase + bono);
        cliente.agregarRegistroPuntos(new RegistroPuntos(transaccion.getFecha(), puntosBase + bono,
                transaccion.obtenerTipo() + " " + transaccion.getCodigo() + " por " + Formato.dinero(transaccion.getMonto()),
                transaccion));
        actualizarRango(cliente);
    }

    public void canjearBeneficio(Cliente cliente, Beneficio beneficio) throws PuntosInsuficientesException {
        if (cliente == null || beneficio == null) {
            throw new IllegalArgumentException("Seleccione el cliente y el beneficio.");
        }
        if (cliente.getPuntosAcumulados() < beneficio.getCostoPuntos()) {
            throw new PuntosInsuficientesException("Necesitas " + beneficio.getCostoPuntos()
                    + " puntos y tienes " + cliente.getPuntosAcumulados() + ".");
        }
        beneficio.aplicar(cliente, Fecha.hoy());
        cliente.agregarRegistroPuntos(new RegistroPuntos(Fecha.hoy(), -beneficio.getCostoPuntos(),
                "Canje: " + beneficio.getNombre(), null));
        actualizarRango(cliente);
        cliente.notificar("Canjeaste: " + beneficio.getNombre() + ". Te quedan " + cliente.getPuntosAcumulados() + " puntos.");
    }


    public void restaurarRango(Cliente cliente) {
        cliente.recalcularPuntos();
        cliente.setRango(calcularRango(cliente.getPuntosAcumulados()));
    }

    public String generarReporteClientesPorRango() {
        String reporte = "CLIENTES POR RANGO\n\n";
        for (Rango r : modelo.getListaRangos()) {
            ArrayList<Cliente> delRango = new ArrayList<>(modelo.getListaClientes().stream()
                    .filter(c -> c.getRango() == r)
                    .toList());
            String nombres = delRango.stream()
                    .map(c -> c.getNombre() + " (" + c.getPuntosAcumulados() + " pts)")
                    .collect(Collectors.joining(", "));
            reporte += r.getNombre() + " [" + delRango.size() + "]: " + nombres + "\n";
        }
        return reporte;
    }

    public ArrayList<Rango> getListaRangos() {
        return modelo.getListaRangos();
    }

    public ArrayList<Beneficio> getCatalogoBeneficios() {
        return modelo.getCatalogoBeneficios();
    }
}
