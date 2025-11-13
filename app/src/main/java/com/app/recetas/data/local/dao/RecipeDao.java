package com.app.recetas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.recetas.data.local.entities.Recipe;

import java.util.List;

/**
 * DAO (Data Access Object) para operaciones CRUD en la tabla recipes
 * Room genera automáticamente la implementación de estos métodos
 */
@Dao
public interface RecipeDao {
    
    /**
     * Obtiene todas las recetas ordenadas por fecha de modificación (más recientes primero)
     * Retorna LiveData para observar cambios automáticamente en la UI
     * @return LiveData con lista de todas las recetas
     */
    @Query("SELECT * FROM recipes ORDER BY dateModified DESC")
    LiveData<List<Recipe>> getAllRecipes();
    
    /**
     * Inserta una nueva receta en la base de datos
     * Si ya existe una receta con el mismo ID, la reemplaza
     * @param recipe Receta a insertar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);
    
    /**
     * Elimina una receta específica de la base de datos
     * @param recipe Receta a eliminar
     */
    @Delete
    void deleteRecipe(Recipe recipe);
    
    /**
     * Actualiza una receta existente en la base de datos
     * @param recipe Receta con datos actualizados
     */
    @Update
    void updateRecipe(Recipe recipe);
    
    /**
     * Obtiene la receta modificada más recientemente
     * Usado para mostrar en SharedPreferences cuál fue la última receta tocada
     * @return La receta con dateModified más reciente, o null si no hay recetas
     */
    @Query("SELECT * FROM recipes ORDER BY dateModified DESC LIMIT 1")

    Recipe getLastModifiedRecipe();
    
    /**
     * Busca recetas por nombre (búsqueda local)
     * Útil para filtrar la colección del usuario
     * @param name Nombre o parte del nombre a buscar
     * @return LiveData con recetas que coinciden con el nombre
     */
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :name || '%' ORDER BY dateModified DESC")
    LiveData<List<Recipe>> searchRecipesByName(String name);
    
    /**
     * Obtiene recetas por categoría
     * @param category Categoría a filtrar
     * @return LiveData con recetas de la categoría especificada
     */
    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY dateModified DESC")
    LiveData<List<Recipe>> getRecipesByCategory(String category);
    
    /**
     * Obtiene solo las recetas personales (creadas por el usuario)
     * @return LiveData con recetas donde isPersonal = true
     */
    @Query("SELECT * FROM recipes WHERE isPersonal = 1 ORDER BY dateModified DESC")
    LiveData<List<Recipe>> getPersonalRecipes();
    
    /**
     * Cuenta el total de recetas en la colección
     * Útil para mostrar estadísticas al usuario
     * @return Número total de recetas
     */
    @Query("SELECT COUNT(*) FROM recipes")
    int getRecipeCount();
}
