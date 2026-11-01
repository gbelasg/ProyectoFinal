package co.edu.uniquindio.poo.model;

public class NotificadorWhatsapp implements Notificador {

    @Override
    public void enviar(Cliente cliente, String mensaje) {
        cliente.recibirMensaje("[WhatsApp " + cliente.getTelefono() + "] " + mensaje);
    }
}
