package cl.ryodantech.amigodocente.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                ConfiguracionEntity.class,
                NivelEntity.class,
                EvaluacionEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // Aquí declaramos los DAOs para poder usarlos después
    public abstract NivelDao nivelDao();
    public abstract EvaluacionDao evaluacionDao();
    public abstract ConfiguracionDao configuracionDao();

    private static volatile AppDatabase INSTANCE;

    // Patrón Singleton: solo puede existir UNA base de datos en la app
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "amigo_docente_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}