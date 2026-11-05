package co.edu.uniquindio.poo.model;

public class RegistroPuntos {

    private String fecha;
    private int puntos;
    private String descripcion;
     private Transaccion transaccion;

    public RegistroPuntos(String fecha, int puntos, String descripcion, Transaccion transaccion) {
        this.fecha = fecha;
        this.puntos = puntos;
        this.descripcion = descripcion;
        this.transaccion= transaccion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion =descripcion;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }
    public boolean estaVigente() {
        return transaccion == null || !transaccion.isRevertida();
    }


}