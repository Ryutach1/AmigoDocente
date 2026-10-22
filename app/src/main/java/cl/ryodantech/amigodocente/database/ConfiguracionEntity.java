package cl.ryodantech.amigodocente.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "configuraciones")
public class ConfiguracionEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public double notaMinima;
    public double notaMaxima;
    public double notaAprobacion;
    public double exigencia;
    public int decimales;
    public String modoRedondeo; // "NORMAL" o "HACIA_ARRIBA"
}