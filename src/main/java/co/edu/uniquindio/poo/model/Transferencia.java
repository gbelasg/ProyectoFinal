package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.exceptions.SaldoInsuficienteException;

public class Transferencia extends Transaccion{
    public static final double PORCENTAJE_COMISION = 0.01;
    private Monedero monederoOrigen;
    private Monedero monederoDestino;

    public Transferencia(String codigo, String fecha, double monto, Monedero monederoOrigen, Monedero monederoDestino) {
        super(codigo, fecha, monto);
        this.monederoOrigen = monederoOrigen;
        this.monederoDestino = monederoDestino;
    }
    @Override
    public void ejecutar() throws SaldoInsuficienteException {
        monederoOrigen.disminuirSaldo(getMonto() + getComision());
        monederoDestino.aumentarSaldo(getMonto());
        monederoOrigen.registrarTransaccion(this);
        monederoDestino.registrarTransaccion(this);
    }

    @Override
    public void revertir() throws SaldoInsuficienteException {
        monederoDestino.disminuirSaldo(getMonto());
        monederoOrigen.aumentarSaldo(getMonto() + getComision());
        marcarRevertida();
    }
    @Override
    public int calcularPuntos() {
        return (int) (3 * (getMonto() / 100));
    }
        public double calcularComision(Cliente cliente) {
            return getMonto() * PORCENTAJE_COMISION * (1 - cliente.calcularDescuentoComision());
        }
    @Override
    public String obtenerTipo() {
        return "Transferencia";
    }







}
