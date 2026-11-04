package cl.ryodantech.amigodocente.repositories;

import android.content.Context;

import java.util.List;

import cl.ryodantech.amigodocente.database.AppDatabase;
import cl.ryodantech.amigodocente.database.EvaluacionDao;
import cl.ryodantech.amigodocente.database.EvaluacionEntity;

public class EvaluacionRepository {

    private final EvaluacionDao evaluacionDao;

    public EvaluacionRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.evaluacionDao = db.evaluacionDao();
    }

    public long guardarEvaluacion(EvaluacionEntity evaluacion) {
        return evaluacionDao.insertar(evaluacion);
    }

    public void actualizarEvaluacion(EvaluacionEntity evaluacion) {
        evaluacionDao.actualizar(evaluacion);
    }

    public void eliminarEvaluacion(EvaluacionEntity evaluacion) {
        evaluacionDao.eliminar(evaluacion);
    }

    public List<EvaluacionEntity> obtenerTodas() {
        return evaluacionDao.obtenerTodas();
    }
}
