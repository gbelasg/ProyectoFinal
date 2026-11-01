package co.edu.uniquindio.poo.monederovirtual.model;

import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.Optional;

public class SistemaMonedero {
    private String nombre;
    private int contadorTransacciones;
    private int contadorMonederos;
    private int contadorProgramadas;
    private ObservableList<Cliente> listaClientes;
    private ObservableList<TransaccionProgramada> listaProgramadas;
    private ObservableList<Beneficio> catalogoBeneficio;
    private ObservableList<Rango> listaRangos;

    public SistemaMonedero(String nombre, int contadorTransacciones, int contadorMonederos, int contadorProgramadas, ObservableList<Cliente> listaClientes, ObservableList<TransaccionProgramada> listaProgramadas, ObservableList<Beneficio> catalogoBeneficio, ObservableList<Rango> listaRangos) {
        this.nombre = nombre;
        this.contadorTransacciones = 0;
        this.contadorMonederos = 0;
        this.contadorProgramadas = 0;
        this.listaClientes = listaClientes;
        this.listaProgramadas = listaProgramadas;
        this.catalogoBeneficio = catalogoBeneficio;
        this.listaRangos = listaRangos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getContadorTransacciones() {
        return contadorTransacciones;
    }

    public void setContadorTransacciones(int contadorTransacciones) {
        this.contadorTransacciones = contadorTransacciones;
    }

    public int getContadorMonederos() {
        return contadorMonederos;
    }

    public void setContadorMonederos(int contadorMonederos) {
        this.contadorMonederos = contadorMonederos;
    }

    public int getContadorProgramadas() {
        return contadorProgramadas;
    }

    public void setContadorProgramadas(int contadorProgramadas) {
        this.contadorProgramadas = contadorProgramadas;
    }

    public ObservableList<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(ObservableList<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public ObservableList<TransaccionProgramada> getListaProgramadas() {
        return listaProgramadas;
    }

    public void setListaProgramadas(ObservableList<TransaccionProgramada> listaProgramadas) {
        this.listaProgramadas = listaProgramadas;
    }

    public ObservableList<Beneficio> getCatalogoBeneficio() {
        return catalogoBeneficio;
    }

    public void setCatalogoBeneficio(ObservableList<Beneficio> catalogoBeneficio) {
        this.catalogoBeneficio = catalogoBeneficio;
    }

    public ObservableList<Rango> getListaRangos() {
        return listaRangos;
    }

    public void setListaRangos(ObservableList<Rango> listaRangos) {
        this.listaRangos = listaRangos;
    }
    public String registrarCliente(Cliente cliente) {
        Optional<Cliente> clienteEncontrado = buscarCliente(cliente.getDocumento());

        if (clienteEncontrado.isPresent()) {
            listaClientes.add(cliente);
            return "El cliente fue registrado exitosamente";
        } else {
            return "No se puede registrar, ya existe un cliente con esa información registrado anteriormente.";
        }
    }

    private Optional<Cliente> buscarCliente(String documento) {
        return listaClientes.stream().filter(cliente ->
                documento.equals(cliente.getDocumento())).findFirst();
    }
    public String actualizarCliente(String documento, String nombre, String correo, String telefono) {
        Optional<Cliente> clienteEncontrado = buscarCliente(documento);

        if (clienteEncontrado.isPresent()) {
            Cliente cliente = clienteEncontrado.get();
            cliente.setNombre(nombre);
            cliente.setCorreo(correo);
            cliente.setTelefono(telefono);
            return "Cliente actualizado exitosamente.";
        } else {
            return "No se encontró ningún cliente con el documento especificado.";
        }
    }

    public String eliminarCliente(String documento) {
        Optional<Cliente> clienteEncontrado = buscarCliente(documento);

        if (clienteEncontrado.isPresent()) {
            return "El cliente a eliminar no existe en el sistema.";
        }
        Cliente cliente = clienteEncontrado.get();

        // Regla UML: No se elimina si tiene transacciones o programadas activas[cite: 6]
        boolean tieneHistorial = cliente.consultarHistorial() != null && !cliente.consultarHistorial().isEmpty();
        boolean tieneProgramadas = listaProgramadas != null &&
                listaProgramadas.stream().anyMatch(p -> p.perteneceA(cliente));

        if (tieneHistorial || tieneProgramadas) {
            return "No se puede eliminar el cliente porque tiene transacciones o vinculaciones activas.";
        }

        listaClientes.remove(cliente);
        return "Cliente eliminado exitosamente.";
    }
    public String crearMonedero(Cliente cliente, String nombre, double saldoInicial) {
        if (cliente == null) {
            return "Debe especificar un cliente válido para asignar el monedero.";
        }

        String codigo = "MON-" + (++contadorMonederos);
        Monedero monedero = new Monedero(codigo, nombre, saldoInicial);
        cliente.agregarMonedero(monedero);

        return "Monedero creado exitosamente con código: " + codigo;
    }

    public Optional<Monedero> buscarMonedero(String codigo) {
        if (codigo == null || listaClientes == null) {
            return Optional.empty();
        }

        return listaClientes.stream()
                .map(c -> c.buscarMonedero(codigo))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public String actualizarMonedero(String codigo, String nuevoNombre) {
        Optional<Monedero> monederoEncontrado = buscarMonedero(codigo);

        if (monederoEncontrado.isPresent()) {
            Monedero monedero = monederoEncontrado.get();
            monedero.setNombre(nuevoNombre);
            return "Monedero actualizado exitosamente.";
        } else {
            return "No se encontró el monedero con el código especificado.";
        }
    }

    public String eliminarMonedero(Cliente cliente, String codigo) {
        if (cliente == null) {
            return "Cliente no válido.";
        }

        Optional<Monedero> monederoEncontrado = buscarMonedero(codigo);

        if (monederoEncontrado.isPresent()) {
            boolean eliminado = cliente.eliminarMondero(monederoEncontrado.get());
            if (eliminado) {
                return "Monedero eliminado exitosamente.";
            } else {
                return "No se pudo desvincular el monedero del cliente.";
            }
        } else {
            return "El monedero a eliminar no fue encontrado.";
        }
    }



    public Transaccion depositar(Cliente cliente, Monedero destino, double monto){
        String codigo= "Transaccion-"+ (++contadorTransacciones);
        Deposito deposito= new Deposito(codigo, LocalDateTime.now(), monto, destino);
        procesarTransaccion(cliente, deposito);
        return deposito;
    }
    public Transaccion retirar(Cliente cliente, Monedero origen, double monto){
        String codigo= "Transaccion-"+ (++contadorTransacciones);
        Retiro retiro= new Retiro(codigo, LocalDateTime.now(), monto, origen, cliente);
        procesarTransaccion(cliente, retiro);
        return retiro;
    }
    public Transaccion transferir(Cliente emisor, Monedero origen, Cliente receptor, Monedero destino, double monto){
        String codigo= "Transaccion-"+ (++contadorTransacciones);
       Transferencia transferencia= new Transferencia(codigo, LocalDateTime.now(), monto, origen, destino,emisor);
        procesarTransaccion(emisor, transferencia);
        return transferencia;
    }
    private void ejecutarTransaccion(Cliente cliente, Transaccion transaccion){
        Optional.ofNullable(transaccion).ifPresent(Transaccion::ejecutar);
    }
    private void procesarTransaccion(Cliente cliente, Transaccion transaccion){
        if (transaccion !=null){
            ejecutarTransaccion(cliente,transaccion);

            if (cliente !=null && AnalizadorTransacciones.esAtipica(transaccion, cliente.consultarHistorial())){
                transaccion.marcarAtipica();
            }
        }
    }
    public void revertirTransaccion(Cliente cliente, String codigo){
        Optional.ofNullable(cliente).map(Cliente::consultarHistorial).flatMap(historial -> historial.stream().filter(
                t-> t.getCodigo() != null && t.getCodigo().equalsIgnoreCase(codigo))
                .findFirst()).ifPresent(Transaccion::revertir);
    }
    public Rango calcularRango(int puntos){
        return Optional.ofNullable(listaRangos).flatMap(rangos -> rangos.stream()
                .filter(rango -> rango.contienePuntos(puntos)).findFirst()).orElse(null);
    }
    public void canjearBeneficios(Cliente cliente, Beneficio beneficio){
        if(cliente!=null && beneficio!= null){
            beneficio.aplicar(cliente, LocalDateTime.now());
        }
    }
    public TransaccionProgramada programarTransaccion(Cliente cliente, String tipo, Monedero origen, Monedero destino, double monto, LocalDateTime fecha, int frecuenciaMeses){
        String codigo= "Programadas" +(++contadorProgramadas);
        TransaccionProgramada programada= new TransaccionProgramada(codigo, tipo, monto, fecha, frecuenciaMeses, origen, destino);
        Optional.ofNullable(listaProgramadas).ifPresent(lista->lista.add(programada));
        return programada;

    }
    public void actualizarProgramada(Cliente cliente, TransaccionProgramada programada, double monto, LocalDateTime fechas, int frecuenciaMeses){
        Optional.ofNullable(programada).ifPresent(p->p.modificar(monto, fechas, frecuenciaMeses));

    }
    public void eliminarPrograma(Cliente cliente, TransaccionProgramada programada){
        Optional.ofNullable(listaProgramadas).ifPresent(lista-> lista.remove(programada));
    }
    public String procesarProgramadas(LocalDateTime fechaActual) {
        return "Procesadas";
    }

    public String generarReporteClientesPorRango() {
        return "Reporte generado";
    }



}
