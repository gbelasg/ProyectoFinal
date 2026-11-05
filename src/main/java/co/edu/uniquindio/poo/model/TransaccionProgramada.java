package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.util.Fecha;

import java.time.LocalDateTime;

// al llegar su fecha, genera una transaccion real
// tipo Deposito: solo usa monederoDestino
// tipo Transferencia: usa monederoOrigen y monederoDestino
public class TransaccionProgramada {

    private String codigo;
    private String tipo;
    private double monto;
    private String fechaEjecucion;
    private int frecuenciaMeses;
    private boolean activa;
    private Cliente cliente;
    private Monedero monederoOrigen;
    private Monedero monederoDestino;

    public TransaccionProgramada(String codigo, String tipo, double monto, String fechaEjecucion, int frecuenciaMeses,
                                 Cliente cliente, Monedero monederoOrigen, Monedero monederoDestino) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.monto = monto;
        this.fechaEjecucion = fechaEjecucion;
        this.frecuenciaMeses = frecuenciaMeses;
        this.cliente = cliente;
        this.monederoOrigen = monederoOrigen;
        this.monederoDestino = monederoDestino;
        this.activa = true;
    }

    public boolean estaPendiente(String fechaActual) {
        return activa && fechaEjecucion.compareTo(fechaActual) <= 0;
    }


    public Transaccion crearTransaccion(String codigoTransaccion) {
        Transaccion transaccion;
        if (tipo.equals("Deposito")) {
            transaccion = new Deposito(codigoTransaccion, fechaEjecucion, monto, monederoDestino);
        } else {
            transaccion = new Transferencia(codigoTransaccion, fechaEjecucion, monto, monederoOrigen, monederoDestino);
        }
        return transaccion;
    }

    public void avanzarFecha() {
        fechaEjecucion = Fecha.sumarMeses(fechaEjecucion, frecuenciaMeses);
    }

    public void modificar(double monto, String fechaEjecucion, int frecuenciaMeses) {
        this.monto = monto;
        this.fechaEjecucion = fechaEjecucion;
        this.frecuenciaMeses = frecuenciaMeses;
    }

    public boolean usaMonedero(Monedero monedero) {
        return monederoOrigen == monedero || monederoDestino == monedero;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public String getFechaEjecucion() {
        return fechaEjecucion;
    }

    public int getFrecuenciaMeses() {
        return frecuenciaMeses;
    }

    public boolean isActiva() {
        return activa;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Monedero getMonederoOrigen() {
        return monederoOrigen;
    }

    public Monedero getMonederoDestino() {
        return monederoDestino;
    }
}
