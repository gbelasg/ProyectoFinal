package co.edu.uniquindio.poo.model;

public class RegistroPuntos {

    private String fecha;
    private int puntos;
    private String concepto;

    public RegistroPuntos(String fecha, int puntos, String concepto) {
        this.fecha = fecha;
        this.puntos = puntos;
        this.concepto = concepto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
}