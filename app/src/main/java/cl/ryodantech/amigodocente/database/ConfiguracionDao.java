package cl.ryodantech.amigodocente.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ConfiguracionDao {
    @Insert
    long insertar(ConfiguracionEntity configuracion);

    @Query("SELECT * FROM configuraciones LIMIT 1")
    ConfiguracionEntity obtenerConfiguracion();

    @androidx.room.Update
    void actualizar (ConfiguracionEntity configuracion);

}
