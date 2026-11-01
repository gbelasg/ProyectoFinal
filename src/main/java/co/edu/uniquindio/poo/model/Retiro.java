package co.edu.uniquindio.poo.model;

public class Retiro extends Transaccion{
    public static final double CARGO_FIJO = 2.0;
    private Monedero monederoOrigen;

    public Retiro(String codigo, String fecha, double monto, Monedero monederoOrigen) {
        super(codigo, fecha, monto);
        this.monederoOrigen = monederoOrigen;
    }

    @Override
    public void ejecutar() throws SaldoInsuficienteException {
        monederoOrigen.disminuirSaldo(getMonto() + getComision());
        monederoOrigen.registrarTransaccion(this);
    }

    @Override
    public void revertir() {
        monederoOrigen.aumentarSaldo(getMonto() + getComision());
        marcarRevertida();
    }

    @Override
    public int calcularPuntos() {
        return (int) (2*(getMonto() / 100));
    }

    @Override
    public double calcularComision(Cliente cliente) {
        double cargo = CARGO_FIJO;
        if (cliente.tieneRetirosSinCargo(getFecha())) {
            cargo = 0;
        }
        return cargo;
    }

    @Override
    public String obtenerTipo() {
        return "Retiro";
    }

}
