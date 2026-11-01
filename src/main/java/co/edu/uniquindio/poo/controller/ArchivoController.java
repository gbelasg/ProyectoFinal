package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Deposito;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.MonederoVirtual;
import co.edu.uniquindio.poo.model.RegistroPuntos;
import co.edu.uniquindio.poo.model.Retiro;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.TransaccionProgramada;
import co.edu.uniquindio.poo.model.Transferencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArchivoController {

    private static final String CARPETA = "datos";
    private static final String SEPARADOR = ";";

    private MonederoVirtual modelo;
    private ClienteController clienteController;
    private PuntosController puntosController;

    public ArchivoController(MonederoVirtual modelo, ClienteController clienteController,
                             PuntosController puntosController) {
        this.modelo = modelo;
        this.clienteController = clienteController;
        this.puntosController = puntosController;
    }

    // GUARDAR

    public void guardar() throws IOException {
        new File(CARPETA).mkdirs();
        guardarClientes();
        guardarMonederos();
        guardarTransacciones();
        guardarPuntos();
        guardarProgramadas();
        guardarNotificaciones();
    }

    private void guardarClientes() throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (Cliente c : modelo.getListaClientes()) {
            lineas.add(unir(c.getDocumento(), c.getNombre(), c.getCorreo(), c.getTelefono(),
                    "" + c.getDescuentoComision(), c.getFechaFinRetirosSinCargo()));
        }
        escribir("clientes.csv", "documento;nombre;correo;telefono;descuentoComision;fechaFinRetirosSinCargo", lineas);
    }


    private void guardarMonederos() throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (Cliente c : modelo.getListaClientes()) {
            for (Monedero m : c.getListaMonederos()) {
                lineas.add(unir(m.getCodigo(), c.getDocumento(), m.getNombre(), "" + m.getSaldo(),
                        "" + m.getSaldoMinimoAlerta()));
            }
        }
        escribir("monederos.csv", "codigo;documentoCliente;nombre;saldo;saldoMinimoAlerta", lineas);
    }


    private void guardarTransacciones() throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (Transaccion t : todasLasTransacciones()) {
            String origen = "";
            String destino = "";
            if (t instanceof Deposito) {
                destino = ((Deposito) t).getMonederoDestino().getCodigo();
            } else if (t instanceof Retiro) {
                origen = ((Retiro) t).getMonederoOrigen().getCodigo();
            } else if (t instanceof Transferencia) {
                origen = ((Transferencia) t).getMonederoOrigen().getCodigo();
                destino = ((Transferencia) t).getMonederoDestino().getCodigo();
            }
            lineas.add(unir(t.getCodigo(), t.obtenerTipo(), t.getFecha(), "" + t.getMonto(), "" + t.getComision(),
                    "" + t.getPuntosGenerados(), "" + t.isAtipica(), "" + t.isRevertida(), origen, destino));
        }
        escribir("transacciones.csv",
                "codigo;tipo;fecha;monto;comision;puntos;atipica;revertida;monederoOrigen;monederoDestino", lineas);
    }

    private void guardarPuntos() throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (Cliente c : modelo.getListaClientes()) {
            for (RegistroPuntos r : c.getHistorialPuntos()) {
                String codigoTransaccion = "";
                if (r.getTransaccion() != null) {
                    codigoTransaccion = r.getTransaccion().getCodigo();
                }
                lineas.add(unir(c.getDocumento(), r.getFecha(), "" + r.getPuntos(), r.getDescripcion(), codigoTransaccion));
            }
        }
        escribir("puntos.csv", "documentoCliente;fecha;puntos;descripcion;codigoTransaccion", lineas);
    }

    private void guardarProgramadas() throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (TransaccionProgramada p : modelo.getListaProgramadas()) {
            String origen = "";
            if (p.getMonederoOrigen() != null) {
                origen = p.getMonederoOrigen().getCodigo();
            }
            lineas.add(unir(p.getCodigo(), p.getTipo(), "" + p.getMonto(), p.getFechaEjecucion(),
                    "" + p.getFrecuenciaMeses(), p.getCliente().getDocumento(), origen, p.getMonederoDestino().getCodigo()));
        }
        escribir("programadas.csv",
                "codigo;tipo;monto;fechaEjecucion;frecuenciaMeses;documentoCliente;monederoOrigen;monederoDestino", lineas);
    }

    private void guardarNotificaciones() throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (Cliente c : modelo.getListaClientes()) {
            for (String mensaje : c.getMensajesRecibidos()) {
                lineas.add(unir(c.getDocumento(), mensaje));
            }
        }
        escribir("notificaciones.csv", "documentoCliente;mensaje", lineas);
    }

    // CARGAR

    public boolean cargar() throws IOException {
        boolean hayDatos = new File(CARPETA, "clientes.csv").exists();
        if (hayDatos) {
            cargarClientes();
            ArrayList<Monedero> monederos = cargarMonederos();
            ArrayList<Transaccion> transacciones = cargarTransacciones(monederos);
            cargarPuntos(transacciones);
            cargarProgramadas(monederos);
            cargarNotificaciones();
            for (Cliente c : modelo.getListaClientes()) {
                puntosController.restaurarRango(c);
            }
            modelo.restaurarContadores(mayorNumero(transacciones.stream().map(Transaccion::getCodigo).toList()),
                    mayorNumero(monederos.stream().map(Monedero::getCodigo).toList()),
                    mayorNumero(modelo.getListaProgramadas().stream().map(TransaccionProgramada::getCodigo).toList()));
        }
        return hayDatos;
    }

    private void cargarClientes() throws IOException {
        for (String[] d : leer("clientes.csv")) {
            Cliente cliente = new Cliente(d[0], d[1], d[2], d[3]);
            cliente.setDescuentoComision(Double.parseDouble(d[4]));
            cliente.setFechaFinRetirosSinCargo(d[5]);
            clienteController.agregarClienteGuardado(cliente);
        }
    }

    private ArrayList<Monedero> cargarMonederos() throws IOException {
        ArrayList<Monedero> monederos = new ArrayList<>();
        for (String[] d : leer("monederos.csv")) {
            Monedero monedero = new Monedero(d[0], d[2], Double.parseDouble(d[4]));
            monedero.restaurarSaldo(Double.parseDouble(d[3]));
            buscarCliente(d[1]).agregarMonedero(monedero);
            monederos.add(monedero);
        }
        return monederos;
    }

    // Las transacciones se vuelven a poner en el historial de sus monederos, sin ejecutarlas otra vez
    private ArrayList<Transaccion> cargarTransacciones(ArrayList<Monedero> monederos) throws IOException {
        ArrayList<Transaccion> transacciones = new ArrayList<>();
        for (String[] d : leer("transacciones.csv")) {
            String tipo = d[1];
            double monto = Double.parseDouble(d[3]);
            Monedero origen = buscarMonedero(monederos, d[8]);
            Monedero destino = buscarMonedero(monederos, d[9]);
            Transaccion t;
            if (tipo.equals("Deposito")) {
                t = new Deposito(d[0], d[2], monto, destino);
                destino.registrarTransaccion(t);
            } else if (tipo.equals("Retiro")) {
                t = new Retiro(d[0], d[2], monto, origen);
                origen.registrarTransaccion(t);
            } else {
                t = new Transferencia(d[0], d[2], monto, origen, destino);
                origen.registrarTransaccion(t);
                destino.registrarTransaccion(t);
            }
            t.restaurarEstado(Double.parseDouble(d[4]), Integer.parseInt(d[5]),
                    Boolean.parseBoolean(d[6]), Boolean.parseBoolean(d[7]));
            transacciones.add(t);
        }
        return transacciones;
    }

    private void cargarPuntos(ArrayList<Transaccion> transacciones) throws IOException {
        for (String[] d : leer("puntos.csv")) {
            Transaccion transaccion = transacciones.stream()
                    .filter(t -> t.getCodigo().equals(d[4]))
                    .findFirst()
                    .orElse(null);
            RegistroPuntos registro = new RegistroPuntos(d[1], Integer.parseInt(d[2]), d[3], transaccion);
            buscarCliente(d[0]).getHistorialPuntos().add(registro);
        }
    }

    private void cargarProgramadas(ArrayList<Monedero> monederos) throws IOException {
        for (String[] d : leer("programadas.csv")) {
            TransaccionProgramada p = new TransaccionProgramada(d[0], d[1], Double.parseDouble(d[2]), d[3],
                    Integer.parseInt(d[4]), buscarCliente(d[5]),
                    buscarMonedero(monederos, d[6]), buscarMonedero(monederos, d[7]));
            modelo.getListaProgramadas().add(p);
        }
    }

    private void cargarNotificaciones() throws IOException {
        for (String[] d : leer("notificaciones.csv")) {
            buscarCliente(d[0]).getMensajesRecibidos().add(d[1]);
        }
    }

    // AYUDAS

    private void escribir(String archivo, String encabezado, ArrayList<String> lineas) throws IOException {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(new File(CARPETA, archivo), StandardCharsets.UTF_8))) {
            escritor.println(encabezado);
            for (String linea : lineas) {
                escritor.println(linea);
            }
        }
    }


    private ArrayList<String[]> leer(String archivo) throws IOException {
        ArrayList<String[]> filas = new ArrayList<>();
        File file = new File(CARPETA, archivo);
        if (file.exists()) {
            try (BufferedReader lector = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String linea = lector.readLine();
                linea = lector.readLine();
                while (linea != null) {
                    if (!linea.isBlank()) {
                        filas.add(linea.split(SEPARADOR, -1));
                    }
                    linea = lector.readLine();
                }
            }
        }
        return filas;
    }

    private String unir(String... datos) {
        String linea = "";
        for (int i = 0; i < datos.length; i++) {
            String dato = datos[i].replace(SEPARADOR, ",").replace("\n", " ");
            if (i > 0) {
                linea += SEPARADOR;
            }
            linea += dato;
        }
        return linea;
    }

    private ArrayList<Transaccion> todasLasTransacciones() {
        return new ArrayList<>(modelo.getListaClientes().stream()
                .flatMap(c -> c.getListaMonederos().stream())
                .flatMap(m -> m.getListaTransacciones().stream())
                .distinct()
                .sorted(Comparator.comparingInt(Transaccion::getNumero))
                .toList());
    }

    private Cliente buscarCliente(String documento) {
        return modelo.getListaClientes().stream()
                .filter(c -> c.getDocumento().equals(documento))
                .findFirst()
                .orElse(null);
    }

    private Monedero buscarMonedero(ArrayList<Monedero> monederos, String codigo) {
        return monederos.stream()
                .filter(m -> m.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    private int mayorNumero(List<String> codigos) {
        return codigos.stream()
                .mapToInt(c -> Integer.parseInt(c.substring(1)))
                .max()
                .orElse(0);
    }
}
