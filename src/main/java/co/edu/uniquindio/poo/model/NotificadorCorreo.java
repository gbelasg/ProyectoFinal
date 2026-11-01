package co.edu.uniquindio.poo.model;

public class NotificadorCorreo implements Notificador {

    @Override
    public void enviar(Cliente cliente, String mensaje) {
        cliente.recibirMensaje("[Correo " + cliente.getCorreo() + "] " + mensaje);
    }
}
