package co.edu.uniquindio.poo.monederovirtual.model;

import com.sun.security.ntlm.Client;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Transaccion {

    protected String codigo;
    protected LocalDateTime fecha;
    protected double monto;
    protected double comision;
    protected int puntosGenerados;
    protected boolean atipica;
    protected boolean revertida;

    public Transaccion(String codigo, LocalDateTime fecha, double monto) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.monto = 0.0;
        this.comision = 0.0;
        this.puntosGenerados = 0;
        this.atipica = false;
        this.revertida = false;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getComision() {
        return comision;
    }

    public void setComision(double comision) {
        this.comision = comision;
    }

    public int getPuntosGenerados() {
        return puntosGenerados;
    }

    public void setPuntosGenerados(int puntosGenerados) {
        this.puntosGenerados = puntosGenerados;
    }

    public boolean isAtipica() {
        return atipica;
    }

    public void setAtipica(boolean atipica) {
        this.atipica = atipica;
    }

    public boolean isRevertida() {
        return revertida;
    }

    public void setRevertida(boolean revertida) {
        this.revertida = revertida;
    }

    public abstract void ejecutar();
    public void revertir(){
        if (!this.revertida){
            this.revertida= true;
        }
    }
    public int calcularPuntos(){
        return this.puntosGenerados;
    }
    public double calcularComision(Cliente cliente){
        return this.comision;
    }
    public String obtenerTipo(){
        return this.getClass().getSimpleName();
    }
    public void marcarAtipica(){
        this.atipica= true;
    }
}
