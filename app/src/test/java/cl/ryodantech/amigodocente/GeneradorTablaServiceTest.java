package cl.ryodantech.amigodocente;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cl.ryodantech.amigodocente.models.ConfiguracionUTP;
import cl.ryodantech.amigodocente.models.Evaluacion;
import cl.ryodantech.amigodocente.models.TablaNotas;
import cl.ryodantech.amigodocente.services.GeneradorTablaService;

public class GeneradorTablaServiceTest {

    @Test
    public void generaTablaCompletaCorrectamente() {
        // 1. Preparamos los datos de prueba
        ConfiguracionUTP config = new ConfiguracionUTP();
        config.setExigencia(0.60); // 60%

        Evaluacion eval = new Evaluacion();
        eval.setId(1);
        eval.setPuntajeMaximo(60.0);

        // 2. Ejecutamos el servicio
        TablaNotas tabla = GeneradorTablaService.generar(eval, config);

        // 3. Verificamos los resultados
        assertNotNull(tabla);
        assertEquals(1, tabla.getEvaluacionId());

        // Una tabla de 0 a 60 debe tener exactamente 61 elementos (incluyendo el 0)
        assertEquals(61, tabla.getItems().size());

        // El primer elemento (0 puntos) debe ser nota 1.0
        assertEquals(0.0, tabla.getItems().get(0).getPuntaje(), 0.001);
        assertEquals(1.0, tabla.getItems().get(0).getNota(), 0.001);

        // El elemento 36 (60%) debe ser nota 4.0
        assertEquals(36.0, tabla.getItems().get(36).getPuntaje(), 0.001);
        assertEquals(4.0, tabla.getItems().get(36).getNota(), 0.001);

        // El último elemento (60 puntos) debe ser nota 7.0
        assertEquals(60.0, tabla.getItems().get(60).getPuntaje(), 0.001);
        assertEquals(7.0, tabla.getItems().get(60).getNota(), 0.001);
    }
}