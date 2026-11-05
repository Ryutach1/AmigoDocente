package cl.ryodantech.amigodocente.models;

public class ConfiguracionUTP {

    public enum ModoRedondeo { NORMAL, HACIA_ARRIBA }

    private long id;
    private double notaMinima = 1.0;
    private double notaMaxima = 7.0;
    private double notaAprobacion = 4.0;
    private double exigencia = 0.60;          // 60% para el 4.0

    public ConfiguracionUTP() { }

    public void validar() {
        if (notaMinima >= notaAprobacion || notaAprobacion >= notaMaxima)
            throw new IllegalStateException("Escala inválida");
        if (exigencia <= 0.0 || exigencia >= 1.0)
            throw new IllegalStateException("Exigencia debe estar entre 0 y 1");
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public double getNotaMinima() { return notaMinima; }
    public void setNotaMinima(double v) { this.notaMinima = v; }
    public double getNotaMaxima() { return notaMaxima; }
    public void setNotaMaxima(double v) { this.notaMaxima = v; }
    public double getNotaAprobacion() { return notaAprobacion; }
    public void setNotaAprobacion(double v) { this.notaAprobacion = v; }
    public double getExigencia() { return exigencia; }
    public void setExigencia(double v) { this.exigencia = v; }

}