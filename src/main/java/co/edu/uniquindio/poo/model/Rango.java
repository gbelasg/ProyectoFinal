package co.edu.uniquindio.poo.model;

public class Rango {

    private String nombre;
    private double descuento;
    private double bonoPuntos;

    public Rango(String nombre, double descuento, double bonoPuntos) {
        this.nombre = nombre;
        this.descuento = descuento;
        this.bonoPuntos = bonoPuntos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getBonoPuntos() {
        return bonoPuntos;
    }

    public void setBonoPuntos(double bonoPuntos) {
        this.bonoPuntos = bonoPuntos;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
