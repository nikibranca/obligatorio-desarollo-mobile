package com.app.recetas.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.app.recetas.data.local.dao.RecipeDao;
import com.app.recetas.data.local.database.AppDatabase;
import com.app.recetas.data.local.entities.Recipe;
import com.app.recetas.data.remote.api.ApiClient;
import com.app.recetas.data.remote.api.MealApiService;
import com.app.recetas.data.remote.dto.AreaResponse;
import com.app.recetas.data.remote.dto.CategoryResponse;
import com.app.recetas.data.remote.dto.MealResponse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

/**
 * Repositorio principal que maneja tanto datos locales (Room) como remotos (API)
 * Implementa patrón Repository para centralizar el acceso a datos
 * Actúa como single source of truth para los ViewModels
 */
public class RecipeRepository {
    
    // DAO para operaciones en base de datos local
    private RecipeDao recipeDao;
    
    // Servicio para llamadas a la API externa
    private MealApiService apiService;
    
    // LiveData con todas las recetas locales (se actualiza automáticamente)
    private LiveData<List<Recipe>> allRecipes;
    
    // Executor para operaciones en background thread
    private ExecutorService executor;
    
    /**
     * Constructor del repositorio
     * Inicializa la base de datos local y el servicio API
     * @param application Contexto de aplicación para inicializar Room
     */
    public RecipeRepository(Application application) {
        // Obtener instancia de la base de datos
        AppDatabase database = AppDatabase.getDatabase(application);
        recipeDao = database.recipeDao();
        
        // Obtener servicio API
        apiService = ApiClient.getApiService();
        
        // Inicializar LiveData con todas las recetas
        allRecipes = recipeDao.getAllRecipes();
        
        // Crear executor con 4 threads para operaciones en background
        executor = Executors.newFixedThreadPool(4);
    }
    
    // ==================== OPERACIONES LOCALES ====================
    
    /**
     * Obtiene todas las recetas almacenadas localmente
     * Retorna LiveData que se actualiza automáticamente cuando cambia la BD
     * @return LiveData con lista de recetas ordenadas por fecha de modificación
     */
    public LiveData<List<Recipe>> getAllRecipes() {
        return allRecipes;
    }
    
    /**
     * Inserta una nueva receta en la base de datos local
     * Operación asíncrona ejecutada en background thread
     * @param recipe Receta a insertar
     */
    public void insertRecipe(Recipe recipe) {
        executor.execute(() -> {
            recipeDao.insertRecipe(recipe);
        });
    }
    
    /**
     * Inserta receta de forma síncrona (para usar en threads ya existentes)
     * @param recipe Receta a insertar
     */
    public void insertRecipeSync(Recipe recipe) {
        recipeDao.insertRecipe(recipe);
    }
    
    /**
     * Elimina una receta de la base de datos local
     * Operación asíncrona ejecutada en background thread
     * @param recipe Receta a eliminar
     */
    public void deleteRecipe(Recipe recipe) {
        executor.execute(() -> {
            recipeDao.deleteRecipe(recipe);
        });
    }
    
    /**
     * Actualiza una receta existente en la base de datos
     * Actualiza automáticamente el timestamp de modificación
     * @param recipe Receta con datos actualizados
     */
    public void updateRecipe(Recipe recipe) {
        executor.execute(() -> {
            // Actualizar timestamp de modificación
            recipe.setDateModified(System.currentTimeMillis());
            recipeDao.updateRecipe(recipe);
        });
    }
    
    /**
     * Obtiene la receta modificada más recientemente
     * Operación síncrona - debe llamarse desde background thread
     * @return Receta más reciente o null si no hay recetas
     */
    public Recipe getLastModifiedRecipe() {
        return recipeDao.getLastModifiedRecipe();
    }
    
    /**
     * Busca recetas locales por nombre
     * @param name Nombre o parte del nombre a buscar
     * @return LiveData con recetas que coinciden
     */
    public LiveData<List<Recipe>> searchLocalRecipesByName(String name) {
        return recipeDao.searchRecipesByName(name);
    }
    
    /**
     * Obtiene recetas locales por categoría
     * @param category Categoría a filtrar
     * @return LiveData con recetas de la categoría
     */
    public LiveData<List<Recipe>> getRecipesByCategory(String category) {
        return recipeDao.getRecipesByCategory(category);
    }
    
    /**
     * Obtiene solo las recetas personales (creadas por el usuario)
     * @return LiveData con recetas personales
     */
    public LiveData<List<Recipe>> getPersonalRecipes() {
        return recipeDao.getPersonalRecipes();
    }
    
    // ==================== OPERACIONES REMOTAS (API) ====================
    
    /**
     * Busca recetas en TheMealDB por nombre
     * @param name Nombre de la receta a buscar
     * @return Call para ejecutar la búsqueda de forma asíncrona
     */
    public Call<MealResponse> searchRecipesByName(String name) {
        return apiService.searchByName(name);
    }
    
    /**
     * Busca recetas en TheMealDB por categoría
     * @param category Categoría exacta a buscar
     * @return Call para ejecutar la búsqueda de forma asíncrona
     */
    public Call<MealResponse> searchRecipesByCategory(String category) {
        return apiService.searchByCategory(category);
    }
    
    /**
     * Busca recetas en TheMealDB por área geográfica
     * @param area Área geográfica exacta a buscar
     * @return Call para ejecutar la búsqueda de forma asíncrona
     */
    public Call<MealResponse> searchRecipesByArea(String area) {
        return apiService.searchByArea(area);
    }
    
    /**
     * Obtiene todas las categorías disponibles en TheMealDB
     * Para poblar spinner de categorías en la búsqueda
     * @return Call con lista de categorías
     */
    public Call<CategoryResponse> getCategories() {
        return apiService.getCategories();
    }
    
    /**
     * Obtiene todas las áreas geográficas disponibles en TheMealDB
     * Para poblar spinner de áreas en la búsqueda
     * @return Call con lista de áreas
     */
    public Call<AreaResponse> getAreas() {
        return apiService.getAreas();
    }
    
    /**
     * Obtiene detalles completos de una receta por ID
     * @param id ID de la receta en TheMealDB
     * @return Call con detalles de la receta
     */
    public Call<MealResponse> getRecipeById(String id) {
        return apiService.getRecipeById(id);
    }
    
    /**
     * Obtiene una receta aleatoria de TheMealDB
     * Funcionalidad extra para sugerir recetas al usuario
     * @return Call con receta aleatoria
     */
    public Call<MealResponse> getRandomRecipe() {
        return apiService.getRandomRecipe();
    }
    
    /**
     * Limpia recursos del repositorio
     * Cierra el executor para evitar memory leaks
     */
    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
