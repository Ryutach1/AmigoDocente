package cl.ryodantech.amigodocente.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "evaluaciones")
public class EvaluacionEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String nombre;
    public double puntajeMaximo;
    public String fecha;
    public long nivelAcademicoId;
    public long cursoId;
    public long configuracionId;
}