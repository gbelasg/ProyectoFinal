package co.edu.uniquindio.poo.monederovirtual.model;

import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class Cliente {

    private String documento;
    private String nombre;
    private String correo;
    private String telefono;
    private int puntosAcumulados;
    private double descuentoComision;
    private LocalDate fechaFinRetirosSinCargo;

    private ObservableList<String> mensajeRecibidos; // especial del java fx se sincronica con la lista y te la muestra actualizanddola
    private ObservableList<Monedero> listaMonederos;
    private ObservableList<Notificador> listaNotificadores;
    private ObservableList<RegistroPuntos> historialPuntos;
    private ObservableList<TransaccionesProgramadas> transaccionesProgramadas;

    public Cliente(String documento, String nombre, String correo, String telefono, int puntosAcumulados, double descuentoComision, LocalDate fechaFinRetirosSinCargo, ObservableList<String> mensajeRecibidos, ObservableList<Monedero> listaMonederos, ObservableList<Notificador> listaNotificadores, ObservableList<RegistroPuntos> historialPuntos, ObservableList<TransaccionesProgramadas> transaccionesProgramadas) {
        this.documento = documento;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.puntosAcumulados = 0;
        this.descuentoComision = 0.0;
        this.fechaFinRetirosSinCargo = fechaFinRetirosSinCargo;
        this.mensajeRecibidos = mensajeRecibidos;
        this.listaMonederos = listaMonederos;
        this.listaNotificadores = listaNotificadores;
        this.historialPuntos = historialPuntos;
        this.transaccionesProgramadas = transaccionesProgramadas;
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

    public LocalDate getFechaFinRetirosSinCargo() {
        return fechaFinRetirosSinCargo;
    }

    public void setFechaFinRetirosSinCargo(LocalDate fechaFinRetirosSinCargo) {
        this.fechaFinRetirosSinCargo = fechaFinRetirosSinCargo;
    }

    public ObservableList<String> getMensajeRecibidos() {
        return mensajeRecibidos;
    }

    public void setMensajeRecibidos(ObservableList<String> mensajeRecibidos) {
        this.mensajeRecibidos = mensajeRecibidos;
    }

    public ObservableList<Monedero> getListaMonederos() {
        return listaMonederos;
    }

    public void setListaMonederos(ObservableList<Monedero> listaMonederos) {
        this.listaMonederos = listaMonederos;
    }

    public ObservableList<Notificador> getListaNotificadores() {
        return listaNotificadores;
    }

    public void setListaNotificadores(ObservableList<Notificador> listaNotificadores) {
        this.listaNotificadores = listaNotificadores;
    }

    public ObservableList<RegistroPuntos> getHistorialPuntos() {
        return historialPuntos;
    }

    public void setHistorialPuntos(ObservableList<RegistroPuntos> historialPuntos) {
        this.historialPuntos = historialPuntos;
    }

    public ObservableList<TransaccionesProgramadas> getTransaccionesProgramadas() {
        return transaccionesProgramadas;
    }

    public void setTransaccionesProgramadas(ObservableList<TransaccionesProgramadas> transaccionesProgramadas) {
        this.transaccionesProgramadas = transaccionesProgramadas;
    }

    public void agregarMonedero(Monedero monedero){
        if (monedero != null && !listaMonederos.contains(monedero)){
            listaMonederos.add(monedero);
        }
    }
    public boolean eliminarMondero(Monedero monedero){
        listaMonederos.remove(monedero);
        return false;
    }
    public boolean tieneMovimientos(){
        return listaMonederos.stream().anyMatch(Monedero::tieneSaldoBajo);
    }
    public Monedero buscarMonedero(String nombre){
        return listaMonederos.stream().filter(monedero-> monedero.getNombre() !=null && monedero.getNombre().equalsIgnoreCase(nombre))
                .findFirst().orElse(null);
    }
    public boolean tieneMonedero(Monedero monedero){
        return listaMonederos.contains(monedero);
    }
    public Monedero obtenerMonderoPrincipal(){
        return listaMonederos.stream().findFirst().orElse(null);
    }
    public double consultarSaldoTotal(){
        return listaMonederos.stream().mapToDouble(Monedero::getSaldo).sum();

    }
    /*
    el flatMap y stream: recorre cada monedero y los junta en una lista comun
    collect: agrupa las transacciones combinadas y luego las retorna en un nuevo arraylist
     */
    public ArrayList<Transaccion> consultarHistorial(){
        return listaMonederos.stream().flatMap(monedero -> monedero.getHistorialTransacciones()
                .stream()).collect(Collectors.toCollection(ArrayList::new));

    }
    public void agregarRegistroPuntos(RegistroPuntos registroPuntos){
        if (registroPuntos!=null){
            historialPuntos.add(registroPuntos);
        }

    }
    public void recalcularPuntos(){
        this. puntosAcumulados  = historialPuntos.stream().filter(RegistroPuntos::estaVigente)
                .mapToInt(RegistroPuntos::getPuntos).sum();

    }
    public double calcularDescuentoComision(){
        return this.descuentoComision;
    }
    public boolean tieneRetirosSinCargo(LocalDate fecha) {
    return Optional.ofNullable(fechaFinRetirosSinCargo).map(fechaFin-> !fecha.isAfter(fechaFin))
            .orElse(false);
    }
    public boolean realizoTransaccion(Transaccion transaccion){
        return Optional.ofNullable(transaccion).map(t->Optional.ofNullable(listaMonederos)
                .map(lista->lista.stream().anyMatch(monedero ->
                        monedero.contieneTransaccion(t))).orElse(false))
                .orElse(false);

    }
    public void agregarNotificador(Notificador notificacor){
        Optional.ofNullable(notificacor).filter(n-> listaNotificadores
        != null && !listaNotificadores.contains(n)).ifPresent(listaNotificadores::add);
    }
    public void notificar (String mensaje){
        Optional.ofNullable(listaNotificadores).ifPresent(lista->
                lista.forEach(n->n.enviar(this, mensaje)));
    }
    public void recibirMensaje(String mensaje){
        Optional.ofNullable(mensaje).filter(t->
                mensajeRecibidos != null).ifPresent(mensajeRecibidos::add);
    }


}
