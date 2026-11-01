package co.edu.uniquindio.poo.monederovirtual.model;

import java.time.LocalDateTime;

public class Retiro extends Transaccion{

    public static final double CARGO_FIJO= 2.0;
     private Monedero monederoOrigen;
     private Cliente cliente;

    public Retiro(String codigo, LocalDateTime fecha, double monto, Monedero monederoOrigen, Cliente cliente) {
        super(codigo, fecha, monto);
        this.monederoOrigen = monederoOrigen;
        this.cliente = cliente;
    }

    public Monedero getMonederoOrigen() {
        return monederoOrigen;
    }

    public void setMonederoOrigen(Monedero monederoOrigen) {
        this.monederoOrigen = monederoOrigen;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public void ejecutar() {
        if (monederoOrigen !=null){
            double comisionAplicada= calcularComision(cliente);
            setComision(comisionAplicada);
            setPuntosGenerados(calcularPuntos());
            double totalADescontar= getMonto()+comisionAplicada;

            monederoOrigen.disminuirSaldo(totalADescontar);
            monederoOrigen.registrarTransaccion(this);
        }
    }

    @Override
    public int calcularPuntos() {
        return (int) (2* (getMonto()/100));
    }

    @Override
    public double calcularComision(Cliente cliente) {
        if (cliente != null && cliente.tieneRetirosSinCargo(
                getFecha().toLocalDate())) {
            return 0.0;

        }
        return CARGO_FIJO;
    }

}
