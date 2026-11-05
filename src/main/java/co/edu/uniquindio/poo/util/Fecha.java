package co.edu.uniquindio.poo.util;

import java.time.LocalDate;


public class Fecha {

    public static String hoy() {
        return LocalDate.now().toString();
    }

    public static boolean esValida(String fecha) {
        boolean valida = false;
        if (fecha != null && fecha.length() == 10 && fecha.charAt(4) == '-' && fecha.charAt(7) == '-') {
            try {
                int anio = Integer.parseInt(fecha.substring(0, 4));
                int mes = Integer.parseInt(fecha.substring(5, 7));
                int dia = Integer.parseInt(fecha.substring(8, 10));
                valida = mes >= 1 && mes <= 12 && dia >= 1 && dia <= diasDelMes(mes, anio);
            } catch (NumberFormatException e) {
                valida = false;
            }
        }
        return valida;
    }

    public static int diasDelMes(int mes, int anio) {
        int[] dias = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int resultado = dias[mes - 1];
        boolean bisiesto = anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0);
        if (mes == 2 && bisiesto) {
            resultado = 29;
        }
        return resultado;
    }


    public static String sumarMeses(String fecha, int meses) {
        int anio = Integer.parseInt(fecha.substring(0, 4));
        int mes = Integer.parseInt(fecha.substring(5, 7)) + meses;
        int dia = Integer.parseInt(fecha.substring(8, 10));
        while (mes > 12) {
            mes = mes - 12;
            anio++;
        }
        dia = Math.min(dia, diasDelMes(mes, anio));
        return String.format("%04d-%02d-%02d", anio, mes, dia);
    }
}