package co.edu.uniquindio.poo.model;

public abstract class Beneficio {

    private String nombre;
    private int puntos;

    public Beneficio(String nombre, int puntos) {
        this.nombre = nombre;
        this.puntos = puntos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public abstract void aplicar(Cliente cliente, String fechaActual);
}
