package co.edu.uniquindio.poo.monederovirtual.controller;

import co.edu.uniquindio.poo.monederovirtual.model.Cliente;
import co.edu.uniquindio.poo.monederovirtual.model.Monedero;
import co.edu.uniquindio.poo.monederovirtual.model.SistemaMonedero;

public class MonederoController {
    private SistemaMonedero modelo;

    public MonederoController(SistemaMonedero modelo) {
        this.modelo = modelo;
    }

    public Monedero crearMonedero(Cliente cliente, String nombreMonedero, double saldoMinimoAlerta) {
        if (cliente == null) {
            throw new IllegalArgumentException("Seleccione primero un cliente.");
        }
        validarDatosMonedero(nombreMonedero, saldoMinimoAlerta);
        if (cliente.buscarMonedero(nombreMonedero) != null) {
            throw new IllegalArgumentException("El cliente ya tiene un monedero llamado " + nombreMonedero + ".");
        }
        Monedero monedero = new Monedero(modelo.generarCodigoMonedero(), nombreMonedero, saldoMinimoAlerta);
        cliente.agregarMonedero(monedero);
        return monedero;
    }

    public void actualizarMonedero(Cliente cliente, Monedero monedero, String nombreMonedero, double saldoMinimoAlerta) {
        validarPropietario(cliente, monedero);
        validarDatosMonedero(nombreMonedero, saldoMinimoAlerta);
        Monedero otro = cliente.buscarMonedero(nombreMonedero);
        if (otro != null && otro != monedero) {
            throw new IllegalArgumentException("El cliente ya tiene otro monedero llamado " + nombreMonedero + ".");
        }
        monedero.setNombre(nombreMonedero);
        monedero.setSaldoMinimoAlerta(saldoMinimoAlerta);
    }

    // Solo se elimina un monedero vacio, sin movimientos y que no use ninguna programada
    public void eliminarMonedero(Cliente cliente, Monedero monedero) {
        validarPropietario(cliente, monedero);
        if (monedero.getSaldo() > 0) {
            throw new IllegalArgumentException("El monedero " + monedero.getNombre()
                    + " todavia tiene saldo. Retire o transfiera el dinero primero.");
        }
        if (!monedero.getListaTransacciones().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar el monedero " + monedero.getNombre()
                    + " porque tiene transacciones en su historial.");
        }
        if (modelo.getListaProgramadas().stream().anyMatch(p -> p.usaMonedero(monedero))) {
            throw new IllegalArgumentException("No se puede eliminar el monedero " + monedero.getNombre()
                    + " porque lo usa una transaccion programada.");
        }
        cliente.eliminarMondero(monedero);
    }

    // Verifica que el monedero exista y que sea del cliente (lo usan las transacciones y las programadas)
    public void validarPropietario(Cliente cliente, Monedero monedero) {
        if (cliente == null) {
            throw new IllegalArgumentException("Seleccione primero un cliente.");
        }
        if (monedero == null) {
            throw new IllegalArgumentException("Seleccione un monedero.");
        }
        if (!cliente.tieneMonedero(monedero)) {
            throw new IllegalArgumentException("El monedero " + monedero.getNombre() + " no pertenece a " + cliente.getNombre() + ".");
        }
    }

    private void validarDatosMonedero(String nombreMonedero, double saldoMinimoAlerta) {
        if (nombreMonedero.isEmpty() || nombreMonedero.contains(";")) {
            throw new IllegalArgumentException("El nombre del monedero es obligatorio y no puede contener ;");
        }
        if (saldoMinimoAlerta < 0) {
            throw new IllegalArgumentException("El saldo minimo no puede ser negativo.");
        }
    }
}
