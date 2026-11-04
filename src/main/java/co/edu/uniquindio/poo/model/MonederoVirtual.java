package co.edu.uniquindio.poo.model;


import java.util.ArrayList;


public class MonederoVirtual {
    private String nombre;
    private int contadorTransacciones;
    private int contadorMonederos;
    private int contadorProgramadas;

    private ArrayList<Cliente> listaClientes;
    private ArrayList<Rango> listaRangos;
    private ArrayList<Beneficio> catalogoBeneficios;
    private ArrayList<TransaccionProgramada> listaProgramadas;

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
