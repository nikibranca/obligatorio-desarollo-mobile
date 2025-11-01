package com.app.recetas.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.recetas.data.local.entities.Recipe;
import com.app.recetas.data.repository.RecipeRepository;
import com.app.recetas.utils.PreferencesManager;

import java.util.List;

/**
 * ViewModel para la pantalla principal (HomeFragment)
 * Maneja la lógica de presentación para la lista de recetas del usuario
 * Extiende AndroidViewModel para tener acceso al contexto de aplicación
 */
public class HomeViewModel extends AndroidViewModel {
    
    // Repositorio para acceso a datos
    private RecipeRepository repository;
    
    // Manager para SharedPreferences
    private PreferencesManager preferencesManager;
    
    // LiveData con todas las recetas del usuario
    private LiveData<List<Recipe>> recipes;
    
    // LiveData para mostrar mensajes al usuario
    private MutableLiveData<String> message = new MutableLiveData<>();
    
    // LiveData para mostrar/ocultar loading
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // LiveData para información de la última receta
    private MutableLiveData<String> lastRecipeInfo = new MutableLiveData<>();
    
    /**
     * Constructor del ViewModel
     * @param application Contexto de aplicación para inicializar dependencias
     */
    public HomeViewModel(@NonNull Application application) {
        super(application);
        
        // Inicializar repositorio y preferences
        repository = new RecipeRepository(application);
        preferencesManager = new PreferencesManager(application);
        
        // Obtener LiveData de recetas del repositorio
        recipes = repository.getAllRecipes();
        
        // Cargar información de la última receta
        loadLastRecipeInfo();
    }
    
    // ==================== GETTERS PARA LIVEDATA ====================
    
    /**
     * Obtiene LiveData con todas las recetas del usuario
     * Se actualiza automáticamente cuando cambia la base de datos
     * @return LiveData con lista de recetas ordenadas por fecha de modificación
     */
    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }
    
    /**
     * Obtiene LiveData para mostrar mensajes al usuario
     * @return LiveData con mensajes informativos
     */
    public LiveData<String> getMessage() {
        return message;
    }
    
    /**
     * Obtiene LiveData para controlar indicador de carga
     * @return LiveData boolean para mostrar/ocultar loading
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * Obtiene LiveData con información de la última receta modificada
     * @return LiveData con texto informativo sobre última receta
     */
    public LiveData<String> getLastRecipeInfo() {
        return lastRecipeInfo;
    }
    
    // ==================== OPERACIONES DE RECETAS ====================
    
    /**
     * Elimina una receta de la colección del usuario
     * Muestra confirmación antes de eliminar
     * @param recipe Receta a eliminar
     */
    public void deleteRecipe(Recipe recipe) {
        if (recipe == null) {
            message.setValue("Error: Receta no válida");
            return;
        }
        
        // Mostrar loading
        isLoading.setValue(true);
        
        // Eliminar receta usando el repositorio
        repository.deleteRecipe(recipe);
        
        // Mostrar mensaje de confirmación
        message.setValue("Receta '" + recipe.getName() + "' eliminada correctamente");
        
        // Verificar si era la última receta modificada y limpiar si es necesario
        String[] lastRecipe = preferencesManager.getLastRecipe();
        if (lastRecipe != null && lastRecipe[0].equals(recipe.getId())) {
            preferencesManager.clearLastRecipe();
            lastRecipeInfo.setValue("");
        }
        
        // Ocultar loading
        isLoading.setValue(false);
    }
    
    /**
     * Actualiza las notas personales de una receta
     * @param recipe Receta a actualizar
     * @param newNotes Nuevas notas personales
     */
    public void updateRecipeNotes(Recipe recipe, String newNotes) {
        if (recipe == null) {
            message.setValue("Error: Receta no válida");
            return;
        }
        
        // Actualizar notas y timestamp
        recipe.setPersonalNotes(newNotes);
        
        // Guardar cambios en la base de datos
        repository.updateRecipe(recipe);
        
        // Actualizar información de última receta modificada
        preferencesManager.saveLastRecipe(recipe.getId(), recipe.getName());
        loadLastRecipeInfo();
        
        message.setValue("Notas actualizadas correctamente");
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    
    /**
     * Carga información de la última receta modificada desde SharedPreferences
     * Actualiza el LiveData correspondiente
     */
    private void loadLastRecipeInfo() {
        String[] lastRecipe = preferencesManager.getLastRecipe();
        
        if (lastRecipe != null) {
            String recipeName = lastRecipe[1];
            long timestamp = Long.parseLong(lastRecipe[2]);
            
            // Formatear mensaje con información de la última receta
            String timeAgo = getTimeAgoString(timestamp);
            String info = "Última receta modificada: " + recipeName + " (" + timeAgo + ")";
            lastRecipeInfo.setValue(info);
        } else {
            lastRecipeInfo.setValue("");
        }
    }
    
    /**
     * Convierte timestamp a string legible (ej: "hace 2 horas")
     * @param timestamp Timestamp en milisegundos
     * @return String con tiempo transcurrido
     */
    private String getTimeAgoString(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - timestamp;
        
        // Convertir diferencia a unidades legibles
        long seconds = diffTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return "hace " + days + (days == 1 ? " día" : " días");
        } else if (hours > 0) {
            return "hace " + hours + (hours == 1 ? " hora" : " horas");
        } else if (minutes > 0) {
            return "hace " + minutes + (minutes == 1 ? " minuto" : " minutos");
        } else {
            return "hace unos segundos";
        }
    }
    
    // ==================== FILTROS Y BÚSQUEDAS ====================
    
    /**
     * Busca recetas locales por nombre
     * @param searchTerm Término de búsqueda
     * @return LiveData con recetas filtradas
     */
    public LiveData<List<Recipe>> searchLocalRecipes(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return recipes; // Retornar todas las recetas si no hay término
        }
        return repository.searchLocalRecipesByName(searchTerm);
    }
    
    /**
     * Filtra recetas por categoría
     * @param category Categoría a filtrar
     * @return LiveData con recetas de la categoría
     */
    public LiveData<List<Recipe>> filterByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return recipes; // Retornar todas las recetas si no hay categoría
        }
        return repository.getRecipesByCategory(category);
    }
    
    /**
     * Obtiene solo las recetas personales (creadas por el usuario)
     * @return LiveData con recetas personales
     */
    public LiveData<List<Recipe>> getPersonalRecipes() {
        return repository.getPersonalRecipes();
    }
    
    // ==================== CLEANUP ====================
    
    /**
     * Limpia recursos cuando el ViewModel se destruye
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        // Limpiar recursos del repositorio
        repository.cleanup();
    }
}
