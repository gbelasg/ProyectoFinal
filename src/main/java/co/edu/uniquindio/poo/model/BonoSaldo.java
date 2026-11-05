package co.edu.uniquindio.poo.model;

public class BonoSaldo extends Beneficio {

    private double valor;

    public BonoSaldo(String nombre, int costoPuntos, double valor) {
        super(nombre, costoPuntos);
        this.valor = valor;
    }


    @Override
    public void aplicar(Cliente cliente, String fechaActual) {
        Monedero principal = cliente.obtenerMonederoPrincipal();
        if (principal == null) {
            throw new IllegalArgumentException("El cliente no tiene monederos para recibir el bono.");
        }
        principal.aumentarSaldo(valor);
    }

    public double getValor() {
        return valor;
    }
}

