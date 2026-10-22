package cl.ryodantech.amigodocente.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface EvaluacionDao {
    @Insert
    long insertar(EvaluacionEntity evaluacion);

    @Query("SELECT * FROM evaluaciones")
    List<EvaluacionEntity> obtenerTodas();

    @Query("SELECT * FROM evaluaciones WHERE id = :id")
    EvaluacionEntity obtenerPorId(long id);

    @Query("SELECT * FROM evaluaciones WHERE nivelAcademicoId = :nivelId")
    List<EvaluacionEntity> obtenerPorNivel(long nivelId);
}