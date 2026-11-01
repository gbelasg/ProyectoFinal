package co.edu.uniquindio.poo.model;

public class NotificadorSMS implements Notificador {

    @Override
    public void enviar(Cliente cliente, String mensaje) {
        cliente.recibirMensaje("[SMS " + cliente.getTelefono() + "] " + mensaje);
    }
}
