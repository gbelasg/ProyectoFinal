package co.edu.uniquindio.poo.model;

import java.util.ArrayList;

public class Monedero {
    private String codigo;
    private String nombre;
    private double saldo;
    private double saldoMinimoAlerta;
    private ArrayList<Transaccion> listaTransacciones;

    public Monedero(String codigo, String nombre, double saldo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.saldo = 0;
        this.saldoMinimoAlerta = saldoMinimoAlerta;
        this.listaTransacciones = listaTransacciones;
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

    public ArrayList<Transaccion> getListaTransacciones() {
        return listaTransacciones;
    }

    public void setListaTransacciones(ArrayList<Transaccion> listaTransacciones) {
        this.listaTransacciones = listaTransacciones;
    }
    public void aumentarSaldo(double monto) {
        if (monto > 0) {
            this.saldo += monto;
        }
    }

    public void disminuirSaldo(double monto) {
        if (monto > 0 && this.saldo >= monto) {
            this.saldo -= monto;
        }
    }

    public void registrarTransaccion(Transaccion transaccion) {
        listaTransacciones.add(transaccion);
    }


    public boolean tieneSaldoBajo() {
        return saldo < saldoMinimoAlerta;
    }

    @Override
    public String toString() {
        return "Monedero{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", saldo=" + saldo +
                ", saldoMinimoAlerta=" + saldoMinimoAlerta +
                ", listaTransacciones=" + listaTransacciones +
                '}';
    }
}
