package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.controller.*;
import co.edu.uniquindio.poo.exceptions.*;
import co.edu.uniquindio.poo.model.*;
import co.edu.uniquindio.poo.util.Fecha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoVirtualControllerTest {

    private ClienteController clientes;
    private MonederoController monederos;
    private TransaccionController transacciones;
    private PuntosController puntos;
    private ProgramadaController programadas;
    private Cliente ana;
    private Monedero ahorros;

    @BeforeEach
    public void prepararEscenario() {
        MonederoVirtualController controlador = new MonederoVirtualController("Monedero Virtual");
        clientes = controlador.getClienteController();
        monederos = controlador.getMonederoController();
        transacciones = controlador.getTransaccionController();
        puntos = controlador.getPuntosController();
        programadas = controlador.getProgramadaController();

        ana = new Cliente("123", "Ana", "ana@mail.com", "300");
        clientes.registrarCliente(ana);
        ahorros = monederos.crearMonedero(ana, "Ahorros", 0);
    }

    private Monedero crearMonederoDePedro(Cliente pedro) {
        clientes.registrarCliente(pedro);
        return monederos.crearMonedero(pedro, "Destino", 0);
    }

    private void crearHistorialNormal() throws Exception {
        transacciones.depositar(ana, ahorros, 100);
        transacciones.depositar(ana, ahorros, 120);
        transacciones.depositar(ana, ahorros, 90);
        transacciones.depositar(ana, ahorros, 110);
    }
//
    @Test
    public void pruebaDeposito() throws Exception {
        transacciones.depositar(ana, ahorros, 1000);

        assertEquals(1000, ahorros.getSaldo(), 0.001);
        assertEquals(10, ana.getPuntosAcumulados());
    }

    @Test
    public void pruebaRetiroConSaldo() throws Exception {
        transacciones.depositar(ana, ahorros, 1000);

        transacciones.retirar(ana, ahorros, 300);

        assertNotEquals(1000, ahorros.getSaldo(), 0.001);
        assertEquals(698, ahorros.getSaldo(), 0.001);
    }

    @Test
    public void pruebaRetiroSinSaldo() throws Exception {
        transacciones.depositar(ana, ahorros, 700);

        assertThrows(SaldoInsuficienteException.class, () -> transacciones.retirar(ana, ahorros, 5000));
        assertEquals(700, ahorros.getSaldo(), 0.001);
    }

    @Test
    public void pruebaMontoInvalido() {
        assertThrows(MontoInvalidoException.class, () -> transacciones.depositar(ana, ahorros, -50));
        assertThrows(MontoInvalidoException.class, () -> transacciones.depositar(ana, ahorros, 0));
    }

    @Test
    public void pruebaTransferencia() throws Exception {
        Cliente pedro = new Cliente("999", "Pedro", "pedro@mail.com", "301");
        Monedero destino = crearMonederoDePedro(pedro);
        transacciones.depositar(ana, ahorros, 1000);
        int puntosAntes = ana.getPuntosAcumulados();

        transacciones.transferir(ana, ahorros, pedro, destino, 500);

        assertEquals(495, ahorros.getSaldo(), 0.001);
        assertEquals(500, destino.getSaldo(), 0.001);
        assertEquals(puntosAntes + 15, ana.getPuntosAcumulados());
    }

    @Test
    public void pruebaRevertirDeposito() throws Exception {
        Transaccion deposito = transacciones.depositar(ana, ahorros, 1000);

        transacciones.revertirTransaccion(ana, deposito.getCodigo());

        assertEquals(0, ahorros.getSaldo(), 0.001);
        assertEquals(0, ana.getPuntosAcumulados());
    }

    @Test
    public void pruebaCanjeSinPuntos() throws Exception {
        transacciones.depositar(ana, ahorros, 40000);
        Beneficio bono = puntos.getCatalogoBeneficios().get(2);

        assertThrows(PuntosInsuficientesException.class, () -> puntos.canjearBeneficio(ana, bono));
        assertEquals(400, ana.getPuntosAcumulados());
    }

    @Test
    public void pruebaProgramadasOrdenadas() throws Exception {
        TransaccionProgramada t3 = programadas.programarTransaccion(ana, "Deposito", null, ahorros, 500, "2026-12-01", 1);
        TransaccionProgramada t1 = programadas.programarTransaccion(ana, "Deposito", null, ahorros, 500, "2026-10-01", 1);
        TransaccionProgramada t2 = programadas.programarTransaccion(ana, "Deposito", null, ahorros, 500, "2026-11-01", 1);

        ArrayList<TransaccionProgramada> esperada = new ArrayList<>();
        esperada.add(t1);
        esperada.add(t2);
        esperada.add(t3);

        programadas.ordenarProgramadas();

        assertIterableEquals(esperada, programadas.getListaProgramadas());
    }

    @Test
    public void pruebaCanjeValido() throws Exception {
        transacciones.depositar(ana, ahorros, 120000);
        Beneficio bono = puntos.getCatalogoBeneficios().get(2);

        puntos.canjearBeneficio(ana, bono);

        assertEquals(120050, ahorros.getSaldo(), 0.001);
        assertEquals(200, ana.getPuntosAcumulados());
    }

    @Test
    public void pruebaTransaccionAtipica() throws Exception {
        crearHistorialNormal();

        Transaccion grande = transacciones.depositar(ana, ahorros, 2000);

        assertTrue(grande.isAtipica());
        assertTrue(ana.getMensajesRecibidos().stream().anyMatch(m -> m.contains("ALERTA")));
    }

    @Test
    public void pruebaRangosCasosLimite() {
        assertEquals("Bronce", puntos.calcularRango(500).getNombre());
        assertEquals("Plata", puntos.calcularRango(501).getNombre());
        assertEquals("Platino", puntos.calcularRango(5001).getNombre());
    }

    @Test
    public void pruebaProgramadaFechaFutura() throws Exception {
        String hoy = Fecha.hoy();
        TransaccionProgramada futura = programadas.programarTransaccion(ana, "Deposito", null, ahorros, 500,
                Fecha.sumarMeses(hoy, 1), 1);

        programadas.procesarProgramadas(hoy);

        assertFalse(!futura.estaPendiente(hoy));
        assertEquals(0, ahorros.getSaldo(), 0.001);
    }

    @Test
    public void pruebaMontoNormal() throws Exception {
        crearHistorialNormal();

        Transaccion normal = transacciones.depositar(ana, ahorros, 115);

        assertFalse(normal.isAtipica());
        assertFalse(ana.getMensajesRecibidos().stream().anyMatch(m -> m.contains("ALERTA")));
    }
}
