package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.exceptions.ClienteNoEncontradoException;
import co.edu.uniquindio.poo.exceptions.MontoInvalidoException;
import co.edu.uniquindio.poo.exceptions.SaldoInsuficienteException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.MonederoVirtual;
import co.edu.uniquindio.poo.util.Fecha;

// Controlador general: crea el modelo y todos los controladores, y se los entrega a la vista.
// La vista (MonederoViewController) solo habla con los controladores, nunca con el modelo directamente.
public class MonederoVirtualController {

    private MonederoVirtual modelo;
    private PuntosController puntosController;
    private AnalisisController analisisController;
    private MonederoController monederoController;
    private ClienteController clienteController;
    private TransaccionController transaccionController;
    private ProgramadaController programadaController;
    private ArchivoController archivoController;

    public MonederoVirtualController(String nombre) {
        modelo = new MonederoVirtual(nombre);
        puntosController = new PuntosController(modelo);
        analisisController = new AnalisisController();
        monederoController = new MonederoController(modelo);
        clienteController = new ClienteController(modelo, puntosController);
        transaccionController = new TransaccionController(modelo, monederoController, puntosController, analisisController);
        programadaController = new ProgramadaController(modelo, monederoController, transaccionController);
        archivoController = new ArchivoController(modelo, clienteController, puntosController);
    }

    // Clientes, monederos y movimientos para poder probar todo apenas abre la ventana
    public void cargarDatosDePrueba() throws ClienteNoEncontradoException, MontoInvalidoException, SaldoInsuficienteException {
        clienteController.registrarCliente(new Cliente("1001", "Ana María López", "ana@gmail.com", "3001112233"));
        clienteController.registrarCliente(new Cliente("1002", "Carlos Pérez", "carlos@gmail.com", "3104445566"));
        Cliente ana = clienteController.buscarCliente("1001");
        Cliente carlos = clienteController.buscarCliente("1002");

        Monedero ahorros = monederoController.crearMonedero(ana, "Ahorros", 0);
        Monedero gastos = monederoController.crearMonedero(ana, "Gastos diarios", 1000);
        Monedero principalCarlos = monederoController.crearMonedero(carlos, "Principal", 500);

        transaccionController.depositar(ana, ahorros, 10000);
        transaccionController.depositar(ana, ahorros, 12000);
        transaccionController.depositar(ana, gastos, 11000);
        transaccionController.depositar(ana, gastos, 9000);
        transaccionController.transferir(ana, ahorros, carlos, principalCarlos, 5000);
        transaccionController.retirar(ana, gastos, 300);
        transaccionController.depositar(carlos, principalCarlos, 2000);

        String hoy = Fecha.hoy();
        programadaController.programarTransaccion(ana, "Deposito", null, ahorros, 500, hoy.substring(0, 8) + "01", 1);
        programadaController.programarTransaccion(ana, "Transferencia", ahorros, principalCarlos, 1000, Fecha.sumarMeses(hoy, 1), 1);
    }

    public MonederoVirtual getModelo() {
        return modelo;
    }

    public PuntosController getPuntosController() {
        return puntosController;
    }

    public AnalisisController getAnalisisController() {
        return analisisController;
    }

    public MonederoController getMonederoController() {
        return monederoController;
    }

    public ClienteController getClienteController() {
        return clienteController;
    }

    public TransaccionController getTransaccionController() {
        return transaccionController;
    }

    public ProgramadaController getProgramadaController() {
        return programadaController;
    }

    public ArchivoController getArchivoController() {
        return archivoController;
    }
}