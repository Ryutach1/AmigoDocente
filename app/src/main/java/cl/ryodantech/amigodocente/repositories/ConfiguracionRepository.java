package cl.ryodantech.amigodocente.repositories;

import android.content.Context;

import java.util.List;

import cl.ryodantech.amigodocente.database.AppDatabase;
import cl.ryodantech.amigodocente.database.ConfiguracionDao;
import cl.ryodantech.amigodocente.database.ConfiguracionEntity;
import cl.ryodantech.amigodocente.database.NivelDao;
import cl.ryodantech.amigodocente.database.NivelEntity;

public class ConfiguracionRepository {

    private final ConfiguracionDao configuracionDao;
    private final NivelDao nivelDao;

    public ConfiguracionRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.configuracionDao = db.configuracionDao();
        this.nivelDao = db.nivelDao();
    }

    /**
     * Se llama al iniciar la app. Verifica que exista la configuración base
     * y los niveles escolares. Si no existen, los crea.
     */
    public void inicializarDatosBase() {
        // 1. ¿Hay configuración UTP?
        if (configuracionDao.obtenerConfiguracion() == null) {
            ConfiguracionEntity configDefault = new ConfiguracionEntity();
            configDefault.notaMinima = 1.0;
            configDefault.notaMaxima = 7.0;
            configDefault.notaAprobacion = 4.0;
            configDefault.exigencia = 0.60; // 60% por defecto
            configDefault.decimales = 1;
            configDefault.modoRedondeo = "NORMAL";
            configuracionDao.insertar(configDefault);
        }

        // 2. ¿Hay niveles escolares?
        if (nivelDao.obtenerTodos().isEmpty()) {
            String[] nivelesBase = {
                    "1° Básico", "2° Básico", "3°Básico", "4° Básico", "5° Básico", "6° Básico", "7° Básico", "8° Básico", "1° Medio", "2° Medio", "3° Medio", "4° Medio"
            };
            for (String nombre : nivelesBase) {
                NivelEntity nivel = new NivelEntity();
                nivel.nombre = nombre;
                nivelDao.insertar(nivel);
            }
        }
    }

    // Métodos para que la interfaz consulte los datos
    public ConfiguracionEntity obtenerConfiguracion() {
        return configuracionDao.obtenerConfiguracion();
    }

    public void guardarConfiguracion(ConfiguracionEntity config) {
        configuracionDao.actualizar(config);
    }

    public List<NivelEntity> obtenerNiveles() {
        return nivelDao.obtenerTodos();
    }
}