package co.edu.uniquindio.poo.model;

public class Deposito extends Transaccion{
    private Monedero monederoDestino;

    public Deposito(String codigo, String fecha, double monto, Monedero monederoDestino) {
        super(codigo, fecha, monto);
        this.monederoDestino = monederoDestino;
    }

    public Monedero getMonederoDestino() {
        return monederoDestino;
    }

    public void setMonederoDestino(Monedero monederoDestino) {
        this.monederoDestino = monederoDestino;
    }
    public void ejecutar() {
        monederoDestino.aumentarSaldo(getMonto());
        monederoDestino.registrarTransaccion(this);
    }

    @Override
    public void revertir() throws SaldoInsuficienteException {
        monederoDestino.disminuirSaldo(getMonto());
        marcarRevertida();
    }
    @Override
    public int calcularPuntos() {
        return (int) (getMonto() / 100);
    }

    @Override
    public double calcularComision(Cliente cliente) {
        return 0;
    }
    @Override
    public String obtenerTipo() {
        return "Deposito";
    }



}
