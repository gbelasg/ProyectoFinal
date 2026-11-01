package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;

public class Transferencia extends Transaccion {
    public static final double PORCENTAJE_COMISION=0.01;
    private Monedero monederoOrigen;
    private Monedero monederoDestino;
    private Cliente cliente;

    public Transferencia(String codigo, LocalDateTime fecha, double monto,  Monedero monederoOrigen, Monedero monederoDestino, Cliente cliente) {
        super(codigo, fecha, monto);
        this.monederoOrigen = monederoOrigen;
        this.monederoDestino = monederoDestino;
        this.cliente = cliente;
    }

    public Monedero getMonederoOrigen() {
        return monederoOrigen;
    }

    public void setMonederoOrigen(Monedero monederoOrigen) {
        this.monederoOrigen = monederoOrigen;
    }

    public Monedero getMonederoDestino() {
        return monederoDestino;
    }

    public void setMonederoDestino(Monedero monederoDestino) {
        this.monederoDestino = monederoDestino;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public void ejecutar() {
        if (monederoOrigen!=null && monederoDestino!=null){
            double comisionAplicada= calcularComision(cliente);
            setComision(comisionAplicada);
            setPuntosGenerados(calcularPuntos());
            double totalADescontar= getMonto()+comisionAplicada;
            monederoOrigen.disminuirSaldo(totalADescontar);
            monederoDestino.aumentarSaldo(getMonto());

            monederoOrigen.registrarTransaccion(this);
            monederoDestino.registrarTransaccion(this);
        }
    }

    @Override
    public int calcularPuntos() {
        return (int) (3*(getMonto()/100));
    }

    @Override
    public double calcularComision(Cliente cliente) {
        double comisionBase= getMonto()*PORCENTAJE_COMISION;
        if (cliente != null){
            double descuento= cliente.calcularDescuentoComision();
            return Math.max(0.0, comisionBase-descuento); // math max retorna el valor mayor
        }
        return comisionBase;
    }
}
