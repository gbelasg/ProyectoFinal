package co.edu.uniquindio.poo.model;

public class Rango {

    private String nombre;
    private int puntosMinimos;
    private int puntosMaximos;
    private double descuentoTransferencia;
    private double bonoPuntos;

    public Rango(String nombre, int puntosMinimos, int puntosMaximos) {
        this.nombre = nombre;
        this.puntosMinimos = puntosMinimos;
        this.puntosMaximos = puntosMaximos;
        this.descuentoTransferencia = descuentoTransferencia;
        this.bonoPuntos = bonoPuntos;
    }

    public boolean contienePuntos(int puntos) {
        return puntos >= puntosMinimos && puntos <= puntosMaximos;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntosMinimos() {
        return puntosMinimos;
    }

    public int getPuntosMaximos() {
        return puntosMaximos;
    }

    public double getDescuentoTransferencia() {
        return descuentoTransferencia;
    }

    public double getBonoPuntos() {
        return bonoPuntos;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
