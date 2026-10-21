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

    public double notaDesdePuntaje(double puntajeObtenido, double puntajeTotal) {
        if (puntajeTotal <= 0) throw new IllegalArgumentException("puntajeTotal > 0");
        return notaDesdePorcentaje(clamp(puntajeObtenido / puntajeTotal, 0.0, 1.0));
    }

    public double puntajeDeAprobacion(double puntajeTotal) {
        return config.getExigencia() * puntajeTotal;
    }

    private double redondear(double valor) {
        int d = config.getDecimales();
        double factor = Math.pow(10, d);

        // TRUCO: Redondeamos a 4 decimales primero para evitar el "3.875000001"
        double valorLimpio = Math.round(valor * 10000.0) / 10000.0;

        double escalado = valorLimpio * factor;
        double r = (config.getModoRedondeo() == ConfiguracionUTP.ModoRedondeo.HACIA_ARRIBA)
                ? Math.ceil(escalado)
                : Math.round(escalado);

        return r / factor;
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
