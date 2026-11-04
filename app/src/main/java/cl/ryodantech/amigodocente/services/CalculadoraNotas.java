package cl.ryodantech.amigodocente.services;

import cl.ryodantech.amigodocente.models.ConfiguracionUTP;

public class CalculadoraNotas {

    private final ConfiguracionUTP config;

    public CalculadoraNotas(ConfiguracionUTP config) {
        if (config == null) throw new IllegalArgumentException("config null");
        config.validar();
        this.config = config;
    }

    public double notaDesdePorcentaje(double porcentaje) {
        double p = clamp(porcentaje, 0.0, 1.0);
        double e = config.getExigencia();
        double nMin = config.getNotaMinima();
        double nMax = config.getNotaMaxima();
        double nApr = config.getNotaAprobacion();

        double raw = (p >= e)
                ? nApr + (p - e) * (nMax - nApr) / (1.0 - e)
                : nMin + p * (nApr - nMin) / e;

        return redondear(raw);
    }

    public double notaDesdePuntaje(int puntajeObtenido, int puntajeTotal) {
        if (puntajeTotal <= 0) return 1.0;
        double p = (double) puntajeObtenido / puntajeTotal;
        return notaDesdePorcentaje(p);
    }

    public double puntajeDeAprobacion(int puntajeTotal) {
        return config.getExigencia() * puntajeTotal;
    }

    // REDONDEO SIMPLIFICADO: 1 decimal, hacia arriba y con protección de dobles
    private double redondear(double valor) {
        // Evitamos errores de precisión tipo 3.9500000001
        double valorLimpio = Math.round(valor * 10000.0) / 10000.0;
        return Math.ceil(valorLimpio * 10.0) / 10.0;
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
