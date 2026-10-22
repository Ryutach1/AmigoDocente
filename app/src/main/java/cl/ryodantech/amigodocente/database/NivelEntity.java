package cl.ryodantech.amigodocente.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "niveles")
public class NivelEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String nombre;
}