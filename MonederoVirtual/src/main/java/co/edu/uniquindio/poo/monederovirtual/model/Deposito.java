package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;

public class Deposito extends Transaccion {
    private Monedero monederoDestino;

    public Deposito(String codigo, LocalDateTime fecha, double monto, Monedero monederoDestino) {
        super(codigo, fecha, monto);
        this.monederoDestino = monederoDestino;
    }

    public Monedero getMonederoDestino() {
        return monederoDestino;
    }

    public void setMonederoDestino(Monedero monederoDestino) {
        this.monederoDestino = monederoDestino;
    }

    @Override
    public void ejecutar() {
        if (monederoDestino!=null){
            monederoDestino.aumentarSaldo(getMonto());
            setPuntosGenerados(calcularPuntos());
            setComision(0.0);
            monederoDestino.registrarTransaccion(this);
        }
    }

    @Override
    public int calcularPuntos() {
        return (int) (getMonto()/100);
    }

    @Override
    public double calcularComision(Cliente cliente) {
        return 0.0;
    }
}
