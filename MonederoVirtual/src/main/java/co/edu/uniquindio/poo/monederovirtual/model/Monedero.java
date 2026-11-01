package co.edu.uniquindio.poo.monederovirtual.model;

import javafx.collections.ObservableList;

import java.util.Optional;

public class Monedero {

    private String codigo;
    private String nombre;
    private double saldo;
    private double saldoMinimoAlerta;

    private ObservableList<Transaccion> historialTransacciones;

    public Monedero(String codigo, String nombre, double saldo, double saldoMinimoAlerta, ObservableList<Transaccion> historialTransacciones) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.saldo = 0.0;
        this.saldoMinimoAlerta = 0.0;
        this.historialTransacciones = historialTransacciones;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getSaldoMinimoAlerta() {
        return saldoMinimoAlerta;
    }

    public void setSaldoMinimoAlerta(double saldoMinimoAlerta) {
        this.saldoMinimoAlerta = saldoMinimoAlerta;
    }

    public ObservableList<Transaccion> getHistorialTransacciones() {
        return historialTransacciones;
    }

    public void setHistorialTransacciones(ObservableList<Transaccion> historialTransacciones) {
        this.historialTransacciones = historialTransacciones;
    }
    public void aumentarSaldo(double monto){
        if (monto>0){
            this.saldo+=monto;
        }
    }
    public void disminuirSaldo (double monto){
        if (monto>0 && this.saldo>=monto){
            this.saldo-=monto;
        }
    }
    public void registrarTransaccion(Transaccion transaccion){
        Optional.ofNullable(transaccion).filter(t-> historialTransacciones !=null)
                .ifPresent(historialTransacciones::add);
    }
    public boolean tieneSaldoBajo(){
        return this.saldo<= this.saldoMinimoAlerta;
    }
    public boolean contieneTransaccion(Transaccion transaccion){
        return Optional.ofNullable(historialTransacciones).map(lista->lista.contains(transaccion))
                .orElse(false);
    }
}
