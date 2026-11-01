package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class MonederoVirtual {
    private String nombre;
    private int contadorTransacciones;
    private int contadorMonederos;
    private int contadorProgramadas;

    // Roles (contencion)
    private ArrayList<Cliente> listaClientes;
    private ArrayList<Rango> listaRangos;
    private ArrayList<Beneficio> catalogoBeneficios;
    private ArrayList<TransaccionProgramada> listaProgramadas;

    public MonederoVirtual(String nombre) {
        this.nombre = nombre;
        this.contadorTransacciones = 0;
        this.contadorMonederos = 0;
        this.contadorProgramadas = 0;
        this.listaClientes = new ArrayList<>();
        this.listaRangos = new ArrayList<>();
        this.catalogoBeneficios = new ArrayList<>();
        this.listaProgramadas = new ArrayList<>();
        listaRangos.add(new Rango("Bronce", 0, 500, 0.0, 0.0));
        listaRangos.add(new Rango("Plata", 501, 1000, 0.05, 0.10));
        listaRangos.add(new Rango("Oro", 1001, 5000, 0.10, 0.20));
        listaRangos.add(new Rango("Platino", 5001, Integer.MAX_VALUE, 0.20, 0.30));

        catalogoBeneficios.add(new DescuentoComision("10% menos de comision en transferencias", 100, 0.10));
        catalogoBeneficios.add(new RetirosSinCargo("Un mes sin cargos por retiros", 500, 1));
        catalogoBeneficios.add(new BonoSaldo("Bono de 50 en el monedero principal", 1000, 50));
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

    public ArrayList<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(ArrayList<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public ArrayList<Rango> getListaRangos() {
        return listaRangos;
    }

    public void setListaRangos(ArrayList<Rango> listaRangos) {
        this.listaRangos = listaRangos;
    }

    public ArrayList<Beneficio> getCatalogoBeneficios() {
        return catalogoBeneficios;
    }

    public void setCatalogoBeneficios(ArrayList<Beneficio> catalogoBeneficios) {
        this.catalogoBeneficios = catalogoBeneficios;
    }

    public ArrayList<TransaccionProgramada> getListaProgramadas() {
        return listaProgramadas;
    }

    public void setListaProgramadas(ArrayList<TransaccionProgramada> listaProgramadas) {
        this.listaProgramadas = listaProgramadas;
    }
    public String registrarCliente(Cliente cliente) {
        Optional<Cliente> clienteEncontrado = buscarCliente(cliente.getDocumento());

        if (clienteEncontrado.isPresent()) {
            listaClientes.add(cliente);
            return "El cliente fue registrado exitosamente.";
        } else {
            return "No se puede registrar, ya existe un cliente con esa información registrado anteriormente.";
        }
    }
    public Optional<Cliente> buscarCliente(String documento) {
        return listaClientes.stream()
                .filter(c -> documento.equals(c.getDocumento()))
                .findFirst();
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

        if (clienteEncontrado.isEmpty()) {
            return "El cliente a eliminar no existe en el sistema.";
        }

        Cliente cliente = clienteEncontrado.get();

        // Regla UML: No se elimina un cliente si tiene transacciones o programadas activas
        boolean tieneHistorial = cliente.consultarHistorial() != null && !cliente.consultarHistorial().isEmpty();
        boolean tieneProgramadas = listaProgramadas != null &&
                listaProgramadas.stream().anyMatch(p -> p.perteneceA(cliente));

        if (tieneHistorial || tieneProgramadas) {
            return "No se puede eliminar el cliente porque tiene transacciones o vinculaciones activas.";
        }

        listaClientes.remove(cliente);
        return "Cliente eliminado exitosamente.";
    }
    public String crearMonedero(Cliente cliente, String nombreMonedero, double saldoInicial) {
        if (cliente == null) {
            return "Debe especificar un cliente válido para asignar el monedero.";
        }

        String codigo = "MON-" + (++contadorMonederos);
        Monedero monedero = new Monedero(codigo, nombreMonedero, saldoInicial);
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
            boolean eliminado = cliente.eliminarMonedero(monederoEncontrado.get());
            if (eliminado) {
                return "Monedero eliminado exitosamente.";
            } else {
                return "No se pudo desvincular el monedero del cliente.";
            }
        } else {
            return "El monedero a eliminar no fue encontrado.";
        }
    }
    public Transaccion depositar(Cliente cliente, Monedero destino, double monto) {
        String codigo = "Transaccion" + (++contadorTransacciones);
        Deposito deposito = new Deposito(codigo, LocalDateTime.now(), monto, destino);
        procesarTransaccion(cliente, deposito);
        return deposito;
    }

    public Transaccion retirar(Cliente cliente, Monedero origen, double monto) {
        String codigo = "Transaccion" + (++contadorTransacciones);
        Retiro retiro = new Retiro(codigo, LocalDateTime.now(), monto, origen, cliente);
        procesarTransaccion(cliente, retiro);
        return retiro;
    }

    public Transaccion transferir(Cliente emisor, Monedero origen, Cliente receptor, Monedero destino, double monto) {
        String codigo = "Transaccion" + (++contadorTransacciones);
        Transferencia transferencia = new Transferencia(codigo, LocalDateTime.now(), monto, origen, destino, emisor);
        procesarTransaccion(emisor, transferencia);
        return transferencia;
    }

    private void ejecutarTransaccion(Cliente cliente, Transaccion transaccion) {
        Optional.ofNullable(transaccion).ifPresent(Transaccion::ejecutar);
    }

    private void procesarTransaccion(Cliente cliente, Transaccion transaccion) {
        ejecutarTransaccion(cliente, transaccion);

        if (cliente != null && AnalizadorTransacciones.esAtipica(transaccion, cliente.consultarHistorial())) {
            transaccion.marcarAtipica();
        }
    }

    public String revertirTransaccion(Cliente cliente, String codigoTransaccion) {
        if (cliente == null) {
            return "Cliente inválido.";
        }

        Optional<Transaccion> transaccionEncontrada = Optional.ofNullable(cliente.consultarHistorial())
                .flatMap(historial -> historial.stream()
                        .filter(t -> t.getCodigo() != null && t.getCodigo().equalsIgnoreCase(codigoTransaccion))
                        .findFirst());

        if (transaccionEncontrada.isPresent()) {
            transaccionEncontrada.get().revertir();
            return "Transacción revertida exitosamente.";
        } else {
            return "No se encontró la transacción en el historial del cliente.";
        }
    }

    // ==========================================
    // RANGOS Y BENEFICIOS
    // ==========================================

    public Optional<Rango> calcularRango(int puntos) {
        return listaRangos.stream()
                .filter(rango -> rango.contienePuntos(puntos))
                .findFirst();
    }

    public String canjearBeneficio(Cliente cliente, Beneficio beneficio) {
        if (cliente == null || beneficio == null) {
            return "Datos incompletos para procesar el canje.";
        }
        beneficio.aplicar(cliente, LocalDateTime.now());
        return "Beneficio canjeado exitosamente.";
    }

    // ==========================================
    // TRANSACCIONES PROGRAMADAS
    // ==========================================

    public TransaccionProgramada programarTransaccion(Cliente cliente, String tipo, Monedero origen, Monedero destino, double monto,
                                                      LocalDateTime fecha, int frecuenciaMeses) {
        String codigo = "PROG-" + (++contadorProgramadas);
        TransaccionProgramada programada = new TransaccionProgramada(codigo, tipo, monto, fecha, frecuenciaMeses, origen, destino);
        listaProgramadas.add(programada);
        return programada;
    }

    public String actualizarProgramada(TransaccionProgramada programada, double monto, LocalDateTime fecha, int frecuenciaMeses) {
        if (programada == null) {
            return "Transacción programada no válida.";
        }
        programada.modificar(monto, fecha, frecuenciaMeses);
        return "Transacción programada actualizada exitosamente.";
    }

    public String eliminarProgramada(TransaccionProgramada programada) {
        if (programada != null && listaProgramadas.remove(programada)) {
            return "Transacción programada eliminada exitosamente.";
        } else {
            return "No se pudo eliminar la transacción programada.";
        }
    }

    public void ordenarProgramadas() {
        // Criterio de ordenamiento natural o por fecha
    }

    public String procesarProgramadas(LocalDateTime fechaActual) {
        return "Procesadas exitosamente.";
    }

    public String generarReporteClientesPorRango() {
        return "Reporte generado exitosamente.";
    }
    public String generarCodigoTransaccion() {
        contadorTransacciones++;
        return "T" + contadorTransacciones;
    }

    public String generarCodigoMonedero() {
        contadorMonederos++;
        return "M" + contadorMonederos;
    }

    public String generarCodigoProgramada() {
        contadorProgramadas++;
        return "P" + contadorProgramadas;
    }

    // Solo se usa al leer los archivos: para que los codigos nuevos no se repitan
    public void restaurarContadores(int transacciones, int monederos, int programadas) {
        contadorTransacciones = transacciones;
        contadorMonederos = monederos;
        contadorProgramadas = programadas;
    }

}
