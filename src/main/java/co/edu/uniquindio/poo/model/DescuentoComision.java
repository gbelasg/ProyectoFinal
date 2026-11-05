package co.edu.uniquindio.poo.model;

public class DescuentoComision extends Beneficio {

    private double porcentaje;

    public DescuentoComision(String nombre, int costoPuntos, double porcentaje) {
        super(nombre, costoPuntos);
        this.porcentaje = porcentaje;
    }
    @Override
    public void aplicar(Cliente cliente, String fechaActual) {
        double nuevoDescuento = Math.min(1.0, cliente.getDescuentoComision() + porcentaje);
        cliente.setDescuentoComision(nuevoDescuento);
    }

    public double getPorcentaje() {
        return porcentaje;
    }
}
