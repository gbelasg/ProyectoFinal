package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.util.Fecha;

public class RetirosSinCargo extends Beneficio {

    private int meses;

    public RetirosSinCargo(String nombre, int costoPuntos, int meses) {
        super(nombre, costoPuntos);
        this.meses = meses;
    }

    // Los retiros quedan sin cargo desde hoy hasta dentro de "meses" meses
    @Override
    public void aplicar(Cliente cliente, String fechaActual) {
        cliente.setFechaFinRetirosSinCargo(Fecha.sumarMeses(fechaActual, meses));
    }

    public int getMeses() {
        return meses;
    }
}
