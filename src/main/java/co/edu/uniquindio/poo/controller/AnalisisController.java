package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Deposito;
import co.edu.uniquindio.poo.model.Monedero;
import co.edu.uniquindio.poo.model.Retiro;
import co.edu.uniquindio.poo.model.Transaccion;
import co.edu.uniquindio.poo.model.Transferencia;
import co.edu.uniquindio.poo.util.Formato;

import java.util.ArrayList;
import java.util.Comparator;

public class AnalisisController {

    public static final int MINIMO_HISTORIAL = 3;

    public double calcularPromedio(ArrayList<Transaccion> lista) {
        return lista.stream().mapToDouble(Transaccion::getMonto).average().orElse(0);
    }

    public double calcularDesviacion(ArrayList<Transaccion> lista) {
        double desviacion = 0;
        if (!lista.isEmpty()) {
            double promedio = calcularPromedio(lista);
            double suma = lista.stream()
                    .mapToDouble(t -> Math.pow(t.getMonto() - promedio, 2))
                    .sum();
            desviacion = Math.sqrt(suma / lista.size());
        }
        return desviacion;
    }

    public double calcularLimite(ArrayList<Transaccion> lista) {
        return calcularPromedio(lista) + 2 * calcularDesviacion(lista);
    }

    public boolean esAtipica(Transaccion transaccion, ArrayList<Transaccion> historial) {
        boolean atipica = false;
        if (historial.size() >= MINIMO_HISTORIAL) {
            atipica = transaccion.getMonto() > calcularLimite(historial);
        }
        return atipica;
    }

    public ArrayList<Transaccion> detectarAtipicas(Cliente cliente) {
        ArrayList<Transaccion> validas = obtenerValidas(cliente);
        ArrayList<Transaccion> atipicas = new ArrayList<>();
        if (validas.size() >= MINIMO_HISTORIAL) {
            double limite = calcularLimite(validas);
            atipicas = new ArrayList<>(validas.stream()
                    .filter(t -> t.getMonto() > limite)
                    .toList());
            atipicas.forEach(Transaccion::marcarAtipica);
        }
        return atipicas;
    }

    public ArrayList<Transaccion> obtenerValidas(Cliente cliente) {
        return new ArrayList<>(cliente.consultarHistorial().stream()
                .filter(t -> !t.isRevertida())
                .toList());
    }

    public boolean esGasto(Transaccion t, Cliente cliente) {
        boolean gasto = false;
        if (t instanceof Retiro) {
            gasto = true;
        } else if (t instanceof Transferencia) {
            gasto = cliente.tieneMonedero(((Transferencia) t).getMonederoOrigen());
        }
        return gasto;
    }

    public boolean salioDelMonedero(Transaccion t, Monedero monedero) {
        boolean salio = false;
        if (t instanceof Retiro) {
            salio = ((Retiro) t).getMonederoOrigen() == monedero;
        } else if (t instanceof Transferencia) {
            salio = ((Transferencia) t).getMonederoOrigen() == monedero;
        }
        return salio;
    }

    public String generarReporteGastos(Cliente cliente) {
        ArrayList<Transaccion> validas = obtenerValidas(cliente);
        ArrayList<Transaccion> gastos = new ArrayList<>(validas.stream()
                .filter(t -> esGasto(t, cliente))
                .toList());

        double totalDepositado = validas.stream()
                .filter(t -> t instanceof Deposito)
                .mapToDouble(Transaccion::getMonto).sum();
        double totalRetirado = gastos.stream()
                .filter(t -> t instanceof Retiro)
                .mapToDouble(Transaccion::getMonto).sum();
        double totalTransferido = gastos.stream()
                .filter(t -> t instanceof Transferencia)
                .mapToDouble(Transaccion::getMonto).sum();
        double totalRecibido = validas.stream()
                .filter(t -> t instanceof Transferencia && !esGasto(t, cliente))
                .mapToDouble(Transaccion::getMonto).sum();
        double comisiones = gastos.stream().mapToDouble(Transaccion::getComision).sum();
        double totalGastado = totalRetirado + totalTransferido;

        String reporte = "REPORTE DE PATRONES DE GASTO - " + cliente.getNombre() + "\n\n";
        reporte += "Transacciones analizadas: " + validas.size() + "\n";
        reporte += "Total depositado: " + Formato.dinero(totalDepositado) + "\n";
        reporte += "Total recibido por transferencias: " + Formato.dinero(totalRecibido) + "\n";
        reporte += "Total retirado: " + Formato.dinero(totalRetirado) + porcentaje(totalRetirado, totalGastado) + "\n";
        reporte += "Total transferido a otros: " + Formato.dinero(totalTransferido) + porcentaje(totalTransferido, totalGastado) + "\n";
        reporte += "Total gastado: " + Formato.dinero(totalGastado) + "\n";
        reporte += "Comisiones pagadas: " + Formato.dinero(comisiones) + "\n";

        if (!gastos.isEmpty()) {
            Transaccion mayor = gastos.stream().max(Comparator.comparingDouble(Transaccion::getMonto)).get();
            reporte += "Gasto promedio: " + Formato.dinero(calcularPromedio(gastos)) + "\n";
            reporte += "Mayor gasto: " + mayor.obtenerTipo() + " " + mayor.getCodigo()
                    + " por " + Formato.dinero(mayor.getMonto()) + " el " + mayor.getFecha() + "\n";
        }

        reporte += "\nGasto por monedero:\n";
        for (Monedero m : cliente.getListaMonederos()) {
            double gastoMonedero = gastos.stream()
                    .filter(t -> salioDelMonedero(t, m))
                    .mapToDouble(Transaccion::getMonto).sum();
            reporte += "  " + m.getNombre() + ": " + Formato.dinero(gastoMonedero)
                    + porcentaje(gastoMonedero, totalGastado) + "\n";
        }

        reporte += "\nGasto por mes:\n";
        ArrayList<String> meses = new ArrayList<>(gastos.stream()
                .map(t -> t.getFecha().substring(0, 7))
                .distinct()
                .sorted()
                .toList());
        for (String mes : meses) {
            double gastoMes = gastos.stream()
                    .filter(t -> t.getFecha().startsWith(mes))
                    .mapToDouble(Transaccion::getMonto).sum();
            reporte += "  " + mes + ": " + Formato.dinero(gastoMes) + "\n";
        }
        return reporte;
    }

    public String generarReporteAtipicas(Cliente cliente) {
        ArrayList<Transaccion> validas = obtenerValidas(cliente);
        ArrayList<Transaccion> atipicas = detectarAtipicas(cliente);

        String texto = "DETECCIÓN DE TRANSACCIONES ATÍPICAS - " + cliente.getNombre() + "\n\n";
        texto += "Transacciones analizadas: " + validas.size() + "\n";
        if (validas.size() < MINIMO_HISTORIAL) {
            texto += "Se necesitan al menos " + MINIMO_HISTORIAL + " transacciones para analizar.\n";
        } else {
            texto += "Promedio: " + Formato.dinero(calcularPromedio(validas)) + "\n";
            texto += "Desviación estándar: " + Formato.dinero(calcularDesviacion(validas)) + "\n";
            texto += "Límite (promedio + 2 × desviación): " + Formato.dinero(calcularLimite(validas)) + "\n\n";
            texto += "Transacciones atípicas encontradas: " + atipicas.size() + "\n";
            for (Transaccion t : atipicas) {
                texto += "  " + t.getCodigo() + "  " + t.getFecha() + "  " + t.obtenerTipo()
                        + "  " + Formato.dinero(t.getMonto()) + "\n";
            }
            if (!atipicas.isEmpty()) {
                cliente.notificar("ALERTA: se encontraron " + atipicas.size()
                        + " transacciones atípicas en tu historial.");
            }
        }
        texto += "\nComplejidad: se recorre el historial una vez para el promedio,"
                + " una para la desviación y una para filtrar.";
        return texto;
    }

    private String porcentaje(double parte, double total) {
        String texto = "";
        if (total > 0) {
            texto = String.format (parte * 100 / total);
        }
        return texto;
    }
}
