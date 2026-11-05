package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.util.Fecha;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Cliente {
    private String documento;
    private String nombre;
    private String correo;
    private String telefono;
    private int puntosAcumulados;
    private double descuentoComision;
    private String fechaFinRetirosSinCargo;
    private ArrayList<String> mensajesRecibidos;
    private Rango rango;
    private ArrayList<Monedero> listaMonederos;
    private ArrayList<RegistroPuntos> historialPuntos;
    private ArrayList<Notificador> listaNotificadores;

    public Cliente(String documento, String nombre, String correo, String telefono) {
        this.documento = documento;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.puntosAcumulados =0 ;
        this.descuentoComision = 0;
        this.fechaFinRetirosSinCargo = fechaFinRetirosSinCargo;
        this.mensajesRecibidos = mensajesRecibidos;
        this.rango = rango;
        this.listaMonederos = listaMonederos;
        this.historialPuntos = historialPuntos;
        this.listaNotificadores = listaNotificadores;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getPuntosAcumulados() {
        return puntosAcumulados;
    }

    public void setPuntosAcumulados(int puntosAcumulados) {
        this.puntosAcumulados = puntosAcumulados;
    }

    public double getDescuentoComision() {
        return descuentoComision;
    }

    public void setDescuentoComision(double descuentoComision) {
        this.descuentoComision = descuentoComision;
    }

    public String getFechaFinRetirosSinCargo() {
        return fechaFinRetirosSinCargo;
    }

    public void setFechaFinRetirosSinCargo(String fechaFinRetirosSinCargo) {
        this.fechaFinRetirosSinCargo = fechaFinRetirosSinCargo;
    }

    public ArrayList<String> getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    public void setMensajesRecibidos(ArrayList<String> mensajesRecibidos) {
        this.mensajesRecibidos = mensajesRecibidos;
    }

    public Rango getRango() {
        return rango;
    }

    public void setRango(Rango rango) {
        this.rango = rango;
    }

    public ArrayList<Monedero> getListaMonederos() {
        return listaMonederos;
    }

    public void setListaMonederos(ArrayList<Monedero> listaMonederos) {
        this.listaMonederos = listaMonederos;
    }

    public ArrayList<RegistroPuntos> getHistorialPuntos() {
        return historialPuntos;
    }

    public void setHistorialPuntos(ArrayList<RegistroPuntos> historialPuntos) {
        this.historialPuntos = historialPuntos;
    }

    public ArrayList<Notificador> getListaNotificadores() {
        return listaNotificadores;
    }

    public void setListaNotificadores(ArrayList<Notificador> listaNotificadores) {
        this.listaNotificadores = listaNotificadores;
    }
    public void agregarMonedero(Monedero monedero) {
        listaMonederos.add(monedero);
    }

    public Monedero buscarMonedero(String nombreMonedero) {
        return listaMonederos.stream()
                .filter(m -> m.getNombre().equalsIgnoreCase(nombreMonedero))
                .findFirst()
                .orElse(null);
    }

    public boolean eliminarMonedero(Monedero monedero) {
        listaMonederos.remove(monedero);
        return false;
    }

    // Un cliente "tiene movimientos" si alguno de sus monederos tiene transacciones
    public boolean tieneMovimientos() {
        return listaMonederos.stream().anyMatch(m -> !m.getListaTransacciones().isEmpty());
    }

    public boolean tieneMonedero(Monedero monedero) {
        return listaMonederos.contains(monedero);
    }

    // El monedero principal es el primero que se creo
    public Monedero obtenerMonederoPrincipal() {
        Monedero principal = null;
        if (!listaMonederos.isEmpty()) {
            principal = listaMonederos.get(0);
        }
        return principal;
    }

    public double consultarSaldoTotal() {
        return listaMonederos.stream().mapToDouble(Monedero::getSaldo).sum();
    }


    public ArrayList<Transaccion> consultarHistorial() {
        // Extrae la lista de transacciones de cada monedero y las combina en una sola lista continua, agrupando las transacciones combinadas
        return listaMonederos.stream()
                .flatMap(monedero -> monedero.getListaTransacciones().stream())
                .collect(Collectors.toCollection(ArrayList::new));
    }
    public void agregarRegistroPuntos(RegistroPuntos registro) {
        historialPuntos.add(registro);
        recalcularPuntos();
    }


    //max escoge el valor mayor
    public void recalcularPuntos() {
        int suma = historialPuntos.stream()
                .filter(RegistroPuntos::estaVigente)
                .mapToInt(RegistroPuntos::getPuntos)
                .sum();
        puntosAcumulados = Math.max(0, suma);
    }

    // Descuento del rango + descuento del beneficio canjeado (maximo 100%)
    public double calcularDescuentoComision() {
        double descuentoRango = 0;
        if (rango != null) {
            descuentoRango = rango.getDescuentoTransferencia();
        }
        return Math.min(1.0, descuentoRango + descuentoComision);
    }

    public boolean tieneRetirosSinCargo(String fecha) {
        return !fechaFinRetirosSinCargo.isEmpty() && fecha.compareTo(fechaFinRetirosSinCargo) <= 0;
    }

    public boolean realizoTransaccion(Transaccion transaccion) {
        return historialPuntos.stream().anyMatch(r -> r.getTransaccion() == transaccion);
    }

    public void agregarNotificador(Notificador notificador) {
        listaNotificadores.add(notificador);
    }

    // Envia el mensaje por todos los medios del cliente (polimorfismo con la interfaz)
    public void notificar(String mensaje) {
        listaNotificadores.forEach(n -> n.enviar(this, mensaje));
    }

    public void recibirMensaje(String texto) {
        mensajesRecibidos.add(Fecha.hoy() + " " + texto);
    }




}
