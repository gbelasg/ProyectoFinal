package co.edu.uniquindio.poo.monederovirtual.viewController;

import co.edu.uniquindio.poo.monederovirtual.controller.ClienteController;
import co.edu.uniquindio.poo.monederovirtual.controller.MonederoController;
import co.edu.uniquindio.poo.monederovirtual.controller.SistemaMonederoController;
import co.edu.uniquindio.poo.monederovirtual.controller.TransaccionController;
import co.edu.uniquindio.poo.monederovirtual.model.Cliente;
import co.edu.uniquindio.poo.monederovirtual.model.Monedero;
import co.edu.uniquindio.poo.monederovirtual.model.Transaccion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MonederoViewController {
    private SistemaMonederoController controlador;
    private ClienteController clienteController;
    private MonederoController monederoController;
    private TransaccionController transaccionController;
    private PuntosController puntosController;
    private ProgramadaController programadaController;
    private AnalisisController analisisController;
    private ArchivoController archivoController;
    // Si los archivos no se pudieron leer, no se guarda para no borrar la informacion que habia
    private boolean puedeGuardar = true;

    // ---------------- Parte superior ----------------
    @FXML private ComboBox<Cliente> cmbClienteActivo;
    @FXML private Label lblSaldoTotal;
    @FXML private Label lblPuntos;
    @FXML private Label lblRango;

    // ---------------- Clientes ----------------
    @FXML private TextField txtBuscarDocumento;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> colClienteDocumento;
    @FXML private TableColumn<Cliente, String> colClienteNombre;
    @FXML private TableColumn<Cliente, String> colClienteCorreo;
    @FXML private TableColumn<Cliente, String> colClienteTelefono;
    @FXML private TableColumn<Cliente, String> colClientePuntos;
    @FXML private TableColumn<Cliente, String> colClienteRango;

    // ---------------- Monederos ----------------
    @FXML private TextField txtNombreMonedero;
    @FXML private TextField txtSaldoMinimo;
    @FXML private TableView<Monedero> tblMonederos;
    @FXML private TableColumn<Monedero, String> colMonederoCodigo;
    @FXML private TableColumn<Monedero, String> colMonederoNombre;
    @FXML private TableColumn<Monedero, String> colMonederoSaldo;
    @FXML private TableColumn<Monedero, String> colMonederoMinimo;

    // ---------------- Operaciones ----------------
    @FXML private ComboBox<Monedero> cmbMonederoDeposito;
    @FXML private TextField txtMontoDeposito;
    @FXML private ComboBox<Monedero> cmbMonederoRetiro;
    @FXML private TextField txtMontoRetiro;
    @FXML private ComboBox<Monedero> cmbMonederoOrigen;
    @FXML private ComboBox<Cliente> cmbClienteDestino;
    @FXML private TextField txtMontoTransferencia;
    @FXML private ComboBox<Monedero> cmbMonederoDestino;
    @FXML private TextField txtCodigoRevertir;
    @FXML private Label lblMensajeOperacion;

    // ---------------- Historial ----------------
    @FXML private ComboBox<String> cmbFiltroTipo;
    @FXML private TableView<Transaccion> tblHistorial;
    @FXML private TableColumn<Transaccion, String> colHistCodigo;
    @FXML private TableColumn<Transaccion, String> colHistFecha;
    @FXML private TableColumn<Transaccion, String> colHistTipo;
    @FXML private TableColumn<Transaccion, String> colHistMonto;
    @FXML private TableColumn<Transaccion, String> colHistComision;
    @FXML private TableColumn<Transaccion, String> colHistPuntos;
    @FXML private TableColumn<Transaccion, String> colHistAtipica;
    @FXML private TableColumn<Transaccion, String> colHistRevertida;

    // ---------------- Puntos y beneficios ----------------
    @FXML private ComboBox<Beneficio> cmbBeneficio;
    @FXML private TableView<RegistroPuntos> tblPuntos;
    @FXML private TableColumn<RegistroPuntos, String> colPuntosFecha;
    @FXML private TableColumn<RegistroPuntos, String> colPuntosCantidad;
    @FXML private TableColumn<RegistroPuntos, String> colPuntosDescripcion;

    // ---------------- Programadas ----------------
    @FXML private ComboBox<String> cmbTipoProgramada;
    @FXML private ComboBox<Monedero> cmbMonederoProgramadaOrigen;
    @FXML private TextField txtMontoProgramada;
    @FXML private ComboBox<Cliente> cmbClienteProgramadaDestino;
    @FXML private ComboBox<Monedero> cmbMonederoProgramadaDestino;
    @FXML private TextField txtFechaProgramada;
    @FXML private TextField txtFrecuenciaMeses;
    @FXML private TextField txtFechaHoy;
    @FXML private TableView<TransaccionProgramada> tblProgramadas;
    @FXML private TableColumn<TransaccionProgramada, String> colProgCodigo;
    @FXML private TableColumn<TransaccionProgramada, String> colProgTipo;
    @FXML private TableColumn<TransaccionProgramada, String> colProgMonto;
    @FXML private TableColumn<TransaccionProgramada, String> colProgFecha;
    @FXML private TableColumn<TransaccionProgramada, String> colProgFrecuencia;
    @FXML private TableColumn<TransaccionProgramada, String> colProgActiva;

    // ---------------- Analisis y notificaciones ----------------
    @FXML private TextArea txtReporte;
    @FXML private TextArea txtNotificaciones;

    // JavaFX llama este metodo automaticamente despues de cargar el FXML
    @FXML
    public void initialize() {
        controlador = new SistemaMonederoController("Monedero Virtual");
        clienteController = controlador.getClienteController();
        monederoController = controlador.getMonederoController();
        transaccionController = controlador.getTransaccionController();
        puntosController = controlador.getPuntosController();
        programadaController = controlador.getProgramadaController();
        analisisController = controlador.getAnalisisController();
        archivoController = controlador.getArchivoController();
        configurarTablas();
        cmbFiltroTipo.getItems().addAll("Todos", "Deposito", "Retiro", "Transferencia");
        cmbFiltroTipo.setValue("Todos");
        cmbTipoProgramada.getItems().addAll("Deposito", "Transferencia");
        cmbBeneficio.getItems().addAll(puntosController.getCatalogoBeneficios());
        txtFechaHoy.setText(Fecha.hoy());
        configurarSeleccionDeTablas();
        cargarDatos();
    }

    // Al hacer clic en una fila, sus datos pasan al formulario para actualizarlos o eliminarlos
    private void configurarSeleccionDeTablas() {
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, cliente) -> {
            if (cliente != null) {
                txtDocumento.setText(cliente.getDocumento());
                txtDocumento.setEditable(false);
                txtNombre.setText(cliente.getNombre());
                txtCorreo.setText(cliente.getCorreo());
                txtTelefono.setText(cliente.getTelefono());
            }
        });
        tblMonederos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, monedero) -> {
            if (monedero != null) {
                txtNombreMonedero.setText(monedero.getNombre());
                txtSaldoMinimo.setText("" + monedero.getSaldoMinimoAlerta());
            }
        });
        tblProgramadas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, programada) -> {
            if (programada != null) {
                txtMontoProgramada.setText("" + programada.getMonto());
                txtFechaProgramada.setText(programada.getFechaEjecucion());
                txtFrecuenciaMeses.setText("" + programada.getFrecuenciaMeses());
            }
        });
    }

    // Lee los archivos CSV de la carpeta "datos". La primera vez no existen y se crean datos de prueba.
    private void cargarDatos() {
        try {
            boolean habiaDatos = archivoController.cargar();
            if (habiaDatos) {
                actualizarListasDeClientes();
                if (!clienteController.getListaClientes().isEmpty()) {
                    cmbClienteActivo.setValue(clienteController.getListaClientes().get(0));
                }
                refrescarDatosCliente();
            } else {
                cargarDatosDePrueba();
            }
        } catch (IOException | RuntimeException e) {
            puedeGuardar = false;
            mostrarError("No se pudieron leer los archivos de la carpeta datos: " + e.getMessage()
                    + "\nLos cambios de esta sesión NO se guardarán para no dañar los archivos.");
        }
    }

    private void guardarDatos() {
        if (puedeGuardar) {
            try {
                archivoController.guardar();
            } catch (IOException e) {
                mostrarError("No se pudieron guardar los datos: " + e.getMessage());
            }
        }
    }

    // Cada columna muestra un dato del objeto de su fila (expresiones lambda)
    private void configurarTablas() {
        colClienteDocumento.setCellValueFactory(c -> texto(c.getValue().getDocumento()));
        colClienteNombre.setCellValueFactory(c -> texto(c.getValue().getNombre()));
        colClienteCorreo.setCellValueFactory(c -> texto(c.getValue().getCorreo()));
        colClienteTelefono.setCellValueFactory(c -> texto(c.getValue().getTelefono()));
        colClientePuntos.setCellValueFactory(c -> texto("" + c.getValue().getPuntosAcumulados()));
        colClienteRango.setCellValueFactory(c -> texto(c.getValue().getRango().getNombre()));

        colMonederoCodigo.setCellValueFactory(c -> texto(c.getValue().getCodigo()));
        colMonederoNombre.setCellValueFactory(c -> texto(c.getValue().getNombre()));
        colMonederoSaldo.setCellValueFactory(c -> texto(dinero(c.getValue().getSaldo())));
        colMonederoMinimo.setCellValueFactory(c -> texto(dinero(c.getValue().getSaldoMinimoAlerta())));

        colHistCodigo.setCellValueFactory(c -> texto(c.getValue().getCodigo()));
        colHistFecha.setCellValueFactory(c -> texto(c.getValue().getFecha()));
        colHistTipo.setCellValueFactory(c -> texto(c.getValue().obtenerTipo()));
        colHistMonto.setCellValueFactory(c -> texto(dinero(c.getValue().getMonto())));
        colHistComision.setCellValueFactory(c -> texto(dinero(c.getValue().getComision())));
        colHistPuntos.setCellValueFactory(c -> texto("" + c.getValue().getPuntosGenerados()));
        colHistAtipica.setCellValueFactory(c -> texto(siNo(c.getValue().isAtipica())));
        colHistRevertida.setCellValueFactory(c -> texto(siNo(c.getValue().isRevertida())));

        colPuntosFecha.setCellValueFactory(c -> texto(c.getValue().getFecha()));
        colPuntosCantidad.setCellValueFactory(c -> texto("" + c.getValue().getPuntos()));
        colPuntosDescripcion.setCellValueFactory(c -> texto(c.getValue().getDescripcion()));

        colProgCodigo.setCellValueFactory(c -> texto(c.getValue().getCodigo()));
        colProgTipo.setCellValueFactory(c -> texto(c.getValue().getTipo()));
        colProgMonto.setCellValueFactory(c -> texto(dinero(c.getValue().getMonto())));
        colProgFecha.setCellValueFactory(c -> texto(c.getValue().getFechaEjecucion()));
        colProgFrecuencia.setCellValueFactory(c -> texto("" + c.getValue().getFrecuenciaMeses()));
        colProgActiva.setCellValueFactory(c -> texto(siNo(c.getValue().isActiva())));
    }

    // ---------------- Clientes ----------------

    @FXML
    public void registrarCliente() {
        try {
            Cliente cliente = new Cliente(txtDocumento.getText().trim(), txtNombre.getText().trim(),
                    txtCorreo.getText().trim(), txtTelefono.getText().trim());
            clienteController.registrarCliente(cliente);
            actualizarListasDeClientes();
            limpiarFormularioCliente();
            cmbClienteActivo.setValue(cliente);
            mostrarInfo("Cliente registrado. Ahora créele un monedero en la pestaña Monederos.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void buscarCliente() {
        try {
            Cliente cliente = clienteController.buscarCliente(txtBuscarDocumento.getText().trim());
            tblClientes.getSelectionModel().select(cliente);
            tblClientes.scrollTo(cliente);
            cmbClienteActivo.setValue(cliente);
        } catch (ClienteNoEncontradoException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void actualizarCliente() {
        try {
            Cliente cliente = tblClientes.getSelectionModel().getSelectedItem();
            clienteController.actualizarCliente(cliente, txtNombre.getText().trim(), txtCorreo.getText().trim(),
                    txtTelefono.getText().trim());
            actualizarListasDeClientes();
            refrescarDatosCliente();
            mostrarInfo("Datos de " + cliente.getNombre() + " actualizados.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void eliminarCliente() {
        try {
            Cliente cliente = tblClientes.getSelectionModel().getSelectedItem();
            if (cliente == null) {
                throw new IllegalArgumentException("Seleccione en la tabla el cliente a eliminar.");
            }
            if (confirmar("¿Seguro que desea eliminar a " + cliente.getNombre() + "?")) {
                clienteController.eliminarCliente(cliente);
                limpiarFormularioCliente();
                actualizarListasDeClientes();
                if (cmbClienteActivo.getValue() == null && !clienteController.getListaClientes().isEmpty()) {
                    cmbClienteActivo.setValue(clienteController.getListaClientes().get(0));
                }
                refrescarDatosCliente();
                mostrarInfo("Cliente eliminado.");
            }
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void limpiarFormularioCliente() {
        txtDocumento.clear();
        txtDocumento.setEditable(true);
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        tblClientes.getSelectionModel().clearSelection();
    }

    @FXML
    public void seleccionarCliente() {
        refrescarDatosCliente();
    }

    // ---------------- Monederos ----------------

    @FXML
    public void crearMonedero() {
        try {
            double minimo = leerNumero(txtSaldoMinimo, "saldo mínimo para alerta");
            Monedero monedero = monederoController.crearMonedero(clienteActivo(), txtNombreMonedero.getText().trim(), minimo);
            txtNombreMonedero.clear();
            txtSaldoMinimo.clear();
            refrescarDatosCliente();
            mostrarInfo("Monedero " + monedero + " creado.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void actualizarMonedero() {
        try {
            Monedero monedero = tblMonederos.getSelectionModel().getSelectedItem();
            if (monedero == null) {
                throw new IllegalArgumentException("Seleccione en la tabla el monedero a actualizar.");
            }
            double minimo = leerNumero(txtSaldoMinimo, "saldo mínimo para alerta");
            monederoController.actualizarMonedero(clienteActivo(), monedero, txtNombreMonedero.getText().trim(), minimo);
            txtNombreMonedero.clear();
            txtSaldoMinimo.clear();
            refrescarDatosCliente();
            mostrarInfo("Monedero actualizado.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void eliminarMonedero() {
        try {
            Monedero monedero = tblMonederos.getSelectionModel().getSelectedItem();
            if (monedero == null) {
                throw new IllegalArgumentException("Seleccione en la tabla el monedero a eliminar.");
            }
            if (confirmar("¿Seguro que desea eliminar el monedero " + monedero.getNombre() + "?")) {
                monederoController.eliminarMonedero(clienteActivo(), monedero);
                txtNombreMonedero.clear();
                txtSaldoMinimo.clear();
                refrescarDatosCliente();
                mostrarInfo("Monedero eliminado.");
            }
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    // ---------------- Operaciones ----------------

    @FXML
    public void depositar() {
        try {
            Transaccion t = transaccionController.depositar(clienteActivo(), cmbMonederoDeposito.getValue(),
                    leerNumero(txtMontoDeposito, "monto"));
            txtMontoDeposito.clear();
            despuesDeOperacion(t, "Depósito realizado");
        } catch (MontoInvalidoException | SaldoInsuficienteException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void retirar() {
        try {
            Transaccion t = transaccionController.retirar(clienteActivo(), cmbMonederoRetiro.getValue(),
                    leerNumero(txtMontoRetiro, "monto"));
            txtMontoRetiro.clear();
            despuesDeOperacion(t, "Retiro realizado");
        } catch (MontoInvalidoException | SaldoInsuficienteException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void seleccionarClienteDestino() {
        Cliente receptor = cmbClienteDestino.getValue();
        if (receptor != null) {
            cargarMonederos(cmbMonederoDestino, receptor.getListaMonederos());
        }
    }

    @FXML
    public void transferir() {
        try {
            Transaccion t = transaccionController.transferir(clienteActivo(), cmbMonederoOrigen.getValue(),
                    cmbClienteDestino.getValue(), cmbMonederoDestino.getValue(),
                    leerNumero(txtMontoTransferencia, "monto"));
            txtMontoTransferencia.clear();
            despuesDeOperacion(t, "Transferencia realizada");
        } catch (MontoInvalidoException | SaldoInsuficienteException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void revertirTransaccion() {
        try {
            String codigo = txtCodigoRevertir.getText().trim();
            transaccionController.revertirTransaccion(clienteActivo(), codigo);
            txtCodigoRevertir.clear();
            refrescarDatosCliente();
            lblMensajeOperacion.setText("Transacción " + codigo + " revertida.");
            mostrarInfo("Transacción " + codigo + " revertida. Puntos actuales: "
                    + clienteActivo().getPuntosAcumulados());
        } catch (SaldoInsuficienteException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void despuesDeOperacion(Transaccion t, String mensaje) {
        refrescarDatosCliente();
        String resumen = mensaje + ": " + t.getCodigo() + " por " + dinero(t.getMonto())
                + "\nComisión: " + dinero(t.getComision())
                + "\nPuntos ganados: " + t.getPuntosGenerados();
        lblMensajeOperacion.setText(resumen.replace("\n", "   |   "));
        if (t.isAtipica()) {
            mostrarAdvertencia(resumen + "\n\nALERTA: esta transacción es atípica (supera promedio + 2 × desviación "
                    + "de su historial). Se envió una notificación al cliente.");
        } else {
            mostrarInfo(resumen);
        }
    }

    // ---------------- Historial ----------------

    @FXML
    public void actualizarHistorial() {
        Cliente cliente = cmbClienteActivo.getValue();
        if (cliente != null) {
            String tipo = cmbFiltroTipo.getValue();
            tblHistorial.getItems().setAll(transaccionController.consultarHistorial(cliente, tipo));
        }
    }

    // ---------------- Puntos y beneficios ----------------

    @FXML
    public void canjearBeneficio() {
        try {
            Beneficio beneficio = cmbBeneficio.getValue();
            puntosController.canjearBeneficio(clienteActivo(), beneficio);
            refrescarDatosCliente();
            mostrarInfo("Canjeaste: " + beneficio.getNombre() + ".\nTe quedan "
                    + clienteActivo().getPuntosAcumulados() + " puntos.");
        } catch (PuntosInsuficientesException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    // ---------------- Programadas ----------------

    @FXML
    public void seleccionarClienteProgramadaDestino() {
        Cliente destino = cmbClienteProgramadaDestino.getValue();
        if (destino != null) {
            cargarMonederos(cmbMonederoProgramadaDestino, destino.getListaMonederos());
        }
    }

    @FXML
    public void programarTransaccion() {
        try {
            int frecuencia = (int) leerNumero(txtFrecuenciaMeses, "repetir cada (meses)");
            TransaccionProgramada p = programadaController.programarTransaccion(clienteActivo(), cmbTipoProgramada.getValue(),
                    cmbMonederoProgramadaOrigen.getValue(), cmbMonederoProgramadaDestino.getValue(),
                    leerNumero(txtMontoProgramada, "monto"), txtFechaProgramada.getText().trim(), frecuencia);
            txtMontoProgramada.clear();
            txtFechaProgramada.clear();
            txtFrecuenciaMeses.clear();
            refrescarDatosCliente();
            mostrarInfo("Transacción programada " + p.getCodigo() + " para el " + p.getFechaEjecucion() + ".");
        } catch (MontoInvalidoException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void actualizarProgramada() {
        try {
            TransaccionProgramada p = tblProgramadas.getSelectionModel().getSelectedItem();
            int frecuencia = (int) leerNumero(txtFrecuenciaMeses, "repetir cada (meses)");
            programadaController.actualizarProgramada(clienteActivo(), p, leerNumero(txtMontoProgramada, "monto"),
                    txtFechaProgramada.getText().trim(), frecuencia);
            txtMontoProgramada.clear();
            txtFechaProgramada.clear();
            txtFrecuenciaMeses.clear();
            refrescarDatosCliente();
            mostrarInfo("Transacción programada " + p.getCodigo() + " actualizada.");
        } catch (MontoInvalidoException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void eliminarProgramada() {
        try {
            TransaccionProgramada p = tblProgramadas.getSelectionModel().getSelectedItem();
            if (p == null) {
                throw new IllegalArgumentException("Seleccione en la tabla la transacción programada a eliminar.");
            }
            if (confirmar("¿Seguro que desea eliminar la transacción programada " + p.getCodigo() + "?")) {
                programadaController.eliminarProgramada(clienteActivo(), p);
                refrescarDatosCliente();
                mostrarInfo("Transacción programada " + p.getCodigo() + " eliminada.");
            }
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void procesarProgramadas() {
        try {
            String resultado = programadaController.procesarProgramadas(txtFechaHoy.getText().trim());
            refrescarDatosCliente();
            mostrarInfo(resultado);
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    // ---------------- Analisis ----------------

    @FXML
    public void generarReporteGastos() {
        try {
            txtReporte.setText(analisisController.generarReporteGastos(clienteActivo()));
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void detectarAtipicas() {
        try {
            txtReporte.setText(analisisController.generarReporteAtipicas(clienteActivo()));
            refrescarDatosCliente();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void generarReporteRangos() {
        txtReporte.setText(puntosController.generarReporteClientesPorRango());
    }

    // ---------------- Notificaciones ----------------

    @FXML
    public void limpiarNotificaciones() {
        Cliente cliente = cmbClienteActivo.getValue();
        if (cliente != null) {
            clienteController.limpiarNotificaciones(cliente);
            refrescarDatosCliente();
        }
    }

    // ---------------- Refrescar la ventana ----------------

    // Se llama despues de cada cambio: guarda en los archivos y actualiza la ventana
    private void refrescarDatosCliente() {
        guardarDatos();
        tblClientes.refresh();
        Cliente cliente = cmbClienteActivo.getValue();
        if (cliente != null) {
            lblSaldoTotal.setText(dinero(cliente.consultarSaldoTotal()));
            lblPuntos.setText("" + cliente.getPuntosAcumulados());
            lblRango.setText(cliente.getRango().getNombre());

            tblMonederos.getItems().setAll(cliente.getListaMonederos());
            tblMonederos.refresh();
            cargarMonederos(cmbMonederoDeposito, cliente.getListaMonederos());
            cargarMonederos(cmbMonederoRetiro, cliente.getListaMonederos());
            cargarMonederos(cmbMonederoOrigen, cliente.getListaMonederos());
            cargarMonederos(cmbMonederoProgramadaOrigen, cliente.getListaMonederos());
            seleccionarClienteDestino();
            seleccionarClienteProgramadaDestino();

            actualizarHistorial();
            tblHistorial.refresh();
            tblPuntos.getItems().setAll(cliente.getHistorialPuntos());
            tblProgramadas.getItems().setAll(programadaController.obtenerProgramadasDe(cliente));
            tblProgramadas.refresh();
            txtNotificaciones.setText(String.join("\n", cliente.getMensajesRecibidos()));
            txtNotificaciones.setScrollTop(Double.MAX_VALUE);
        } else {
            lblSaldoTotal.setText("$0,00");
            lblPuntos.setText("0");
            lblRango.setText("-");
            tblMonederos.getItems().clear();
            tblHistorial.getItems().clear();
            tblPuntos.getItems().clear();
            tblProgramadas.getItems().clear();
            txtNotificaciones.clear();
        }
    }

    // Llena un ComboBox de monederos sin perder lo que estaba seleccionado
    private void cargarMonederos(ComboBox<Monedero> combo, ObservableList<Monedero> monederos) {
        Monedero seleccionado = combo.getValue();
        combo.getItems().setAll(monederos);
        if (monederos.contains(seleccionado)) {
            combo.setValue(seleccionado);
        } else {
            combo.setValue(null);
        }
    }

    // Vuelve a llenar la tabla y las listas de clientes (despues de crear, actualizar o eliminar)
    private void actualizarListasDeClientes() {
        tblClientes.getItems().setAll(clienteController.getListaClientes());
        tblClientes.refresh();
        cargarClientes(cmbClienteActivo);
        cargarClientes(cmbClienteDestino);
        cargarClientes(cmbClienteProgramadaDestino);
    }

    private void cargarClientes(ComboBox<Cliente> combo) {
        Cliente seleccionado = combo.getValue();
        combo.getItems().setAll(clienteController.getListaClientes());
        if (clienteController.getListaClientes().contains(seleccionado)) {
            combo.setValue(seleccionado);
        } else {
            combo.setValue(null);
        }
    }

    // ---------------- Ayudas ----------------

    private Cliente clienteActivo() {
        Cliente cliente = cmbClienteActivo.getValue();
        if (cliente == null) {
            throw new IllegalArgumentException("Seleccione primero un cliente activo en la parte de arriba.");
        }
        return cliente;
    }

    private double leerNumero(TextField campo, String nombreCampo) {
        try {
            return Double.parseDouble(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " debe ser un número.");
        }
    }

    private SimpleStringProperty texto(String valor) {
        return new SimpleStringProperty(valor);
    }

    private String dinero(double valor) {
        return Formato.dinero(valor);
    }

    private String siNo(boolean valor) {
        String texto = "No";
        if (valor) {
            texto = "Sí";
        }
        return texto;
    }

    private void mostrarInfo(String mensaje) {
        mostrarAlerta(Alert.AlertType.INFORMATION, mensaje);
    }

    private void mostrarAdvertencia(String mensaje) {
        mostrarAlerta(Alert.AlertType.WARNING, mensaje);
    }

    private void mostrarError(String mensaje) {
        mostrarAlerta(Alert.AlertType.ERROR, mensaje);
    }

    private boolean confirmar(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Monedero Virtual");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        ponerIcono(alerta);
        return alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Monedero Virtual");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        ponerIcono(alerta);
        alerta.showAndWait();
    }

    // Los mensajes usan el mismo icono de la ventana principal
    private void ponerIcono(Alert alerta) {
        Stage ventanaMensaje = (Stage) alerta.getDialogPane().getScene().getWindow();
        ventanaMensaje.getIcons().add(new Image(getClass().getResourceAsStream("/co/edu/uniquindio/poo/view/imagenes/icono.png")));
    }

    // ---------------- Datos de prueba ----------------

    // El controlador crea los clientes y movimientos de prueba; aqui solo se muestran en la ventana
    private void cargarDatosDePrueba() {
        try {
            controlador.cargarDatosDePrueba();
            actualizarListasDeClientes();
            cmbClienteActivo.setValue(clienteController.buscarCliente("1001"));
            refrescarDatosCliente();
        } catch (ClienteNoEncontradoException | MontoInvalidoException | SaldoInsuficienteException
                 | IllegalArgumentException e) {
            mostrarError("No se pudieron cargar los datos de prueba: " + e.getMessage());
        }
    }

}
