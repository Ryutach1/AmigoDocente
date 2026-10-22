package cl.ryodantech.amigodocente.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface NivelDao {
    @Insert
    long insertar(NivelEntity nivel);

    @Query("SELECT * FROM niveles")
    List<NivelEntity> obtenerTodos();

    // Para no duplicar niveles (ej: si ya existe "1° Medio", no crearlo de nuevo)
    @Query("SELECT * FROM niveles WHERE nombre = :nombre LIMIT 1")
    NivelEntity buscarPorNombre(String nombre);
}