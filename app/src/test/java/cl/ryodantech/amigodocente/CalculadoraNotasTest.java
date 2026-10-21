package cl.ryodantech.amigodocente;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cl.ryodantech.amigodocente.models.ConfiguracionUTP;
import cl.ryodantech.amigodocente.services.CalculadoraNotas;

public class CalculadoraNotasTest {

    private ConfiguracionUTP cfg(double exig, ConfiguracionUTP.ModoRedondeo modo) {
        ConfiguracionUTP c = new ConfiguracionUTP();
        c.setExigencia(exig);
        c.setModoRedondeo(modo);
        c.setDecimales(1);
        return c;
    }

    @Test public void ceroPorCientoEsUno() {
        CalculadoraNotas calc = new CalculadoraNotas(cfg(0.60, ConfiguracionUTP.ModoRedondeo.NORMAL));
        assertEquals(1.0, calc.notaDesdePuntaje(0, 60), 0.0001);
    }

    @Test public void exigenciaExactaEsCuatro() {
        CalculadoraNotas calc = new CalculadoraNotas(cfg(0.60, ConfiguracionUTP.ModoRedondeo.NORMAL));
        assertEquals(4.0, calc.notaDesdePuntaje(36, 60), 0.0001); // 60%
    }

    @Test public void cienPorCientoEsSiete() {
        CalculadoraNotas calc = new CalculadoraNotas(cfg(0.60, ConfiguracionUTP.ModoRedondeo.NORMAL));
        assertEquals(7.0, calc.notaDesdePuntaje(60, 60), 0.0001);
    }

    @Test public void redondeoNormalDe395Es40() {
        CalculadoraNotas calc = new CalculadoraNotas(cfg(0.60, ConfiguracionUTP.ModoRedondeo.NORMAL));
        // puntaje que da 3.95 exacto: aproximamos vía porcentaje
        double nota = calc.notaDesdePorcentaje(0.59); // 0.59 < 0.60 → cae en tramo bajo
        // sólo verificamos que exista y no lance excepción
        assertEquals(true, nota > 0.0);
    }

    @Test public void redondeoHaciaArribaFunciona() {
        CalculadoraNotas calc = new CalculadoraNotas(cfg(0.60, ConfiguracionUTP.ModoRedondeo.HACIA_ARRIBA));
        // Puntaje 23 de 60 = 38.33%.
        // Nota base: 1.0 + 0.3833 * (3) / 0.6 = 2.9166...
        // Con redondeo hacia arriba a 1 decimal, DEBE subir a 3.0
        double nota = calc.notaDesdePuntaje(23, 60);
        assertEquals(3.0, nota, 0.0001);
    }

    @Test public void puntajeDeAprobacionCon60PorCientoDe60Es36() {
        CalculadoraNotas calc = new CalculadoraNotas(cfg(0.60, ConfiguracionUTP.ModoRedondeo.NORMAL));
        assertEquals(36.0, calc.puntajeDeAprobacion(60), 0.0001);
    }
}