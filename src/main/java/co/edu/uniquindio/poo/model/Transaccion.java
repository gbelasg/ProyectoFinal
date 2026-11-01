package co.edu.uniquindio.poo.model;

public abstract class Transaccion {
    private String codigo;
    private String fecha;
    private double monto;
    private double comision;
    private int puntosGenerados;
    private boolean atipica;
    private boolean revertida;

    public Transaccion(String codigo, String fecha, double monto) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.monto = monto;
        this.comision = 0;
        this.puntosGenerados = 0;
        this.atipica = false;
        this.revertida = false;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getComision() {
        return comision;
    }

    public void setComision(double comision) {
        this.comision = comision;
    }

    public int getPuntosGenerados() {
        return puntosGenerados;
    }

    public void setPuntosGenerados(int puntosGenerados) {
        this.puntosGenerados = puntosGenerados;
    }

    public boolean isAtipica() {
        return atipica;
    }

    public void setAtipica(boolean atipica) {
        this.atipica = atipica;
    }

    public boolean isRevertida() {
        return revertida;
    }

    public void setRevertida(boolean revertida) {
        this.revertida = revertida;
    }
    public abstract void ejecutar() throws SaldoInsuficienteException;

    public abstract void revertir() throws SaldoInsuficienteException;

    public abstract int calcularPuntos();

    public abstract double calcularComision(Cliente cliente);

    public abstract String obtenerTipo();

    public void marcarAtipica() {
        atipica = true;
    }

    protected void marcarRevertida() {
        revertida = true;
    }

    public void restaurarEstado(double comision, int puntosGenerados, boolean atipica, boolean revertida) {
        this.comision = comision;
        this.puntosGenerados = puntosGenerados;
        this.atipica = atipica;
        this.revertida = revertida;
    }

}
