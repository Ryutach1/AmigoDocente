package cl.ryodantech.amigodocente.services;

import java.util.ArrayList;
import java.util.List;

import cl.ryodantech.amigodocente.models.ConfiguracionUTP;
import cl.ryodantech.amigodocente.models.Evaluacion;
import cl.ryodantech.amigodocente.models.ItemTabla;
import cl.ryodantech.amigodocente.models.TablaNotas;

public class GeneradorTablaService {

    /**
     * Genera una tabla completa a partir de una evaluación y la configuración del colegio.
     * Itera desde 0 puntos hasta el puntaje máximo, en pasos de 1 punto.
     */
    public static TablaNotas generar(Evaluacion evaluacion, ConfiguracionUTP config) {
        if (evaluacion == null || config == null) {
            throw new IllegalArgumentException("Evaluación y configuración no pueden ser nulas");
        }

        TablaNotas tabla = new TablaNotas();
        tabla.setEvaluacionId(evaluacion.getId());

        List<ItemTabla> items = new ArrayList<>();
        CalculadoraNotas calculadora = new CalculadoraNotas(config);

        // Iteramos desde 0 hasta el puntaje máximo (ej: si es 60, llega hasta 60)
        int puntajeMaximo = (int) evaluacion.getPuntajeMaximo();

        for (int puntaje = 0; puntaje <= puntajeMaximo; puntaje++) {
            // Calculamos la nota para este puntaje específico
            double nota = calculadora.notaDesdePuntaje(puntaje, puntajeMaximo);

            // Creamos el ítem y lo agregamos a la lista
            ItemTabla item = new ItemTabla(puntaje, nota);
            items.add(item);
        }

        tabla.setItems(items);
        return tabla;
    }
}