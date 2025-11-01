package com.app.recetas.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.app.recetas.data.local.dao.RecipeDao;
import com.app.recetas.data.local.entities.Recipe;

/**
 * Clase principal de la base de datos Room
 * Define las entidades, versión y configuración de la BD
 */
@Database(
    entities = {Recipe.class}, // Lista de entidades (tablas) en la BD
    version = 1, // Versión de la BD (incrementar para migraciones)
    exportSchema = false // No exportar esquema para testing
)
public abstract class AppDatabase extends RoomDatabase {
    
    // Instancia singleton de la base de datos
    private static volatile AppDatabase INSTANCE;
    
    // Nombre del archivo de la base de datos SQLite
    private static final String DATABASE_NAME = "recipe_database";
    
    /**
     * Método abstracto que Room implementa automáticamente
     * Proporciona acceso al DAO de recetas
     * @return Instancia del RecipeDao
     */
    public abstract RecipeDao recipeDao();
    
    /**
     * Obtiene la instancia singleton de la base de datos
     * Implementa patrón Singleton thread-safe con double-checked locking
     * @param context Contexto de la aplicación
     * @return Instancia única de AppDatabase
     */
    public static AppDatabase getDatabase(final Context context) {
        // Primera verificación sin sincronización (más rápida)
        if (INSTANCE == null) {
            // Sincronización para thread-safety
            synchronized (AppDatabase.class) {
                // Segunda verificación dentro del bloque sincronizado
                if (INSTANCE == null) {
                    // Crear la instancia de la base de datos
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(), // Usar Application context para evitar memory leaks
                            AppDatabase.class, // Clase de la base de datos
                            DATABASE_NAME // Nombre del archivo SQLite
                    )
                    // Configuraciones adicionales de Room
                    .fallbackToDestructiveMigration() // En caso de cambio de esquema, recrear BD
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * Método para cerrar la base de datos (opcional)
     * Útil para testing o cuando se necesite limpiar recursos
     */
    public static void closeDatabase() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}
