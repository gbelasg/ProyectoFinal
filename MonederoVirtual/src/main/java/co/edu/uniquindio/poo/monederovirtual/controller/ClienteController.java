package co.edu.uniquindio.poo.monederovirtual.controller;

import co.edu.uniquindio.poo.monederovirtual.model.Cliente;
import co.edu.uniquindio.poo.monederovirtual.model.SistemaMonedero;
import javafx.collections.ObservableList;

public class ClienteController {
    private SistemaMonedero modelo;
    private PuntosController puntosController;

    public ClienteController(SistemaMonedero modelo, PuntosController puntosController) {
        this.modelo = modelo;
        this.puntosController = puntosController;
    }

    public void registrarCliente(Cliente cliente) {
        validarDatosCliente(cliente.getDocumento(), cliente.getNombre(), cliente.getCorreo(), cliente.getTelefono());
        boolean existe = modelo.getListaClientes().stream()
                .anyMatch(c -> c.getDocumento().equals(cliente.getDocumento()));
        if (existe) {
            throw new IllegalArgumentException("Ya existe un cliente con el documento " + cliente.getDocumento() + ".");
        }
        agregarClienteGuardado(cliente);
        cliente.notificar("Bienvenido a " + modelo.getNombre() + ". Tu rango inicial es " + cliente.getRango().getNombre() + ".");
    }

    // Le pone el rango inicial y los tres medios de notificacion, y lo agrega a la lista.
    // Tambien se usa al leer los archivos, porque ahi el cliente no se valida ni se notifica de nuevo.
    public void agregarClienteGuardado(Cliente cliente) {
        cliente.setRango(puntosController.calcularRango(0));
        cliente.agregarNotificador(new NotificadorWhatsapp());
        cliente.agregarNotificador(new NotificadorCorreo());
        cliente.agregarNotificador(new NotificadorSMS());
        modelo.getListaClientes().add(cliente);
    }

    public Cliente buscarCliente(String documento) throws ClienteNoEncontradoException {
        return modelo.getListaClientes().stream()
                .filter(c -> c.getDocumento().equals(documento))
                .findFirst()
                .orElseThrow(() -> new ClienteNoEncontradoException("No existe un cliente con el documento " + documento + "."));
    }

    // El documento no se puede cambiar porque identifica al cliente
    public void actualizarCliente(Cliente cliente, String nombreCliente, String correo, String telefono) {
        if (cliente == null) {
            throw new IllegalArgumentException("Seleccione el cliente a actualizar.");
        }
        validarDatosCliente(cliente.getDocumento(), nombreCliente, correo, telefono);
        cliente.setNombre(nombreCliente);
        cliente.setCorreo(correo);
        cliente.setTelefono(telefono);
        cliente.notificar("Tus datos fueron actualizados.");
    }

    // Solo se elimina un cliente sin movimientos, para no dejar historiales incompletos
    // (por ejemplo, una transferencia que otro cliente recibio de el)
    public void eliminarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Seleccione el cliente a eliminar.");
        }
        if (cliente.tieneMovimientos()) {
            throw new IllegalArgumentException("No se puede eliminar a " + cliente.getNombre()
                    + " porque tiene transacciones en su historial.");
        }
        boolean usadoEnProgramadas = modelo.getListaProgramadas().stream()
                .anyMatch(p -> p.getCliente() == cliente
                        || cliente.getListaMonederos().stream().anyMatch(p::usaMonedero));
        if (usadoEnProgramadas) {
            throw new IllegalArgumentException("No se puede eliminar a " + cliente.getNombre()
                    + " porque tiene transacciones programadas. Elimine primero esas programadas.");
        }
        modelo.getListaClientes().remove(cliente);
    }

    public void limpiarNotificaciones(Cliente cliente) {
        cliente.getMensajesRecibidos().clear();
    }

    private void validarDatosCliente(String documento, String nombreCliente, String correo, String telefono) {
        if (documento.isEmpty() || nombreCliente.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            throw new IllegalArgumentException("Todos los datos del cliente son obligatorios.");
        }
        // El ; separa las columnas en los archivos CSV
        if ((documento + nombreCliente + correo).contains(";")) {
            throw new IllegalArgumentException("Los datos no pueden contener el caracter ;");
        }
        if (!correo.contains("@")) {
            throw new IllegalArgumentException("El correo debe contener @.");
        }
        if (!telefono.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("El telefono solo debe tener numeros.");
        }
    }

    public ObservableList<Cliente> getListaClientes() {
        return modelo.getListaClientes();
    }
}
