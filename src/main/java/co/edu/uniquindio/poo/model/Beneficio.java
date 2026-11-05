package co.edu.uniquindio.poo.model;

public abstract class Beneficio {

    private String nombre;
    private int costoPuntos;

    public Beneficio(String nombre, int costoPuntos) {
        this.nombre = nombre;
        this.costoPuntos = costoPuntos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCostoPuntos() {
        return costoPuntos;
    }

    public void setCostoPuntos(int costoPuntos) {
        this.costoPuntos = costoPuntos;
    }

    public abstract void aplicar(Cliente cliente, String fechaActual);
}
