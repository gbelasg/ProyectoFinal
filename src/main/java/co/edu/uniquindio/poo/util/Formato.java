package co.edu.uniquindio.poo.util;

// mostrar los valores como texto
public class Formato {

    // Ejemplo: 1234.5 -> $1.234,50
    public static String dinero(double valor) {
        return String.format("$%,.2f", valor);
    }
}
