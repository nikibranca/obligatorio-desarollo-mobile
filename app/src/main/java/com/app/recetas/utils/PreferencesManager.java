package com.app.recetas.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase para manejar SharedPreferences de la aplicación
 * Almacena información sobre la última receta modificada/agregada
 * Requerimiento: mostrar cuál fue la última receta ingresada o modificada
 */
public class PreferencesManager {
    
    // Nombre del archivo de preferencias
    private static final String PREF_NAME = "recipe_preferences";
    
    // Claves para almacenar datos en SharedPreferences
    private static final String KEY_LAST_RECIPE_ID = "last_recipe_id";
    private static final String KEY_LAST_RECIPE_NAME = "last_recipe_name";
    private static final String KEY_LAST_RECIPE_TIME = "last_recipe_time";
    private static final String KEY_USER_EMAIL = "user_email"; // Email del usuario logueado
    private static final String KEY_FIRST_LAUNCH = "first_launch"; // Si es el primer lanzamiento
    
    // Instancia de SharedPreferences
    private SharedPreferences preferences;
    
    /**
     * Constructor que inicializa SharedPreferences
     * @param context Contexto de la aplicación
     */
    public PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    // ==================== ÚLTIMA RECETA ====================
    
    /**
     * Guarda información de la última receta modificada/agregada
     * Se llama cada vez que se agrega una receta o se modifican sus notas
     * @param recipeId ID único de la receta
     * @param recipeName Nombre de la receta para mostrar al usuario
     */
    public void saveLastRecipe(String recipeId, String recipeName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LAST_RECIPE_ID, recipeId);
        editor.putString(KEY_LAST_RECIPE_NAME, recipeName);
        editor.putLong(KEY_LAST_RECIPE_TIME, System.currentTimeMillis()); // Timestamp actual
        editor.apply(); // Aplicar cambios de forma asíncrona
    }
    
    /**
     * Obtiene información de la última receta modificada
     * @return Array con [id, nombre, timestamp] o null si no hay datos
     */
    public String[] getLastRecipe() {
        String id = preferences.getString(KEY_LAST_RECIPE_ID, null);
        String name = preferences.getString(KEY_LAST_RECIPE_NAME, null);
        long time = preferences.getLong(KEY_LAST_RECIPE_TIME, 0);
        
        // Solo retornar si tenemos datos válidos
        if (id != null && name != null && time > 0) {
            return new String[]{id, name, String.valueOf(time)};
        }
        return null;
    }
    
    /**
     * Verifica si hay una última receta guardada
     * @return true si existe información de última receta
     */
    public boolean hasLastRecipe() {
        return getLastRecipe() != null;
    }
    
    /**
     * Limpia la información de la última receta
     * Útil cuando se elimina la receta que era la última modificada
     */
    public void clearLastRecipe() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_LAST_RECIPE_ID);
        editor.remove(KEY_LAST_RECIPE_NAME);
        editor.remove(KEY_LAST_RECIPE_TIME);
        editor.apply();
    }
    
    // ==================== INFORMACIÓN DE USUARIO ====================
    
    /**
     * Guarda el email del usuario logueado
     * @param email Email del usuario
     */
    public void saveUserEmail(String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }
    
    /**
     * Obtiene el email del usuario logueado
     * @return Email del usuario o null si no está logueado
     */
    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, null);
    }
    
    /**
     * Limpia el email del usuario (al hacer logout)
     */
    public void clearUserEmail() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_USER_EMAIL);
        editor.apply();
    }
    
    // ==================== CONFIGURACIONES DE APP ====================
    
    /**
     * Marca que la app ya no es el primer lanzamiento
     * Útil para mostrar tutoriales o configuraciones iniciales
     */
    public void setFirstLaunchCompleted() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_FIRST_LAUNCH, false);
        editor.apply();
    }
    
    /**
     * Verifica si es el primer lanzamiento de la app
     * @return true si es el primer lanzamiento
     */
    public boolean isFirstLaunch() {
        return preferences.getBoolean(KEY_FIRST_LAUNCH, true); // Por defecto es true
    }
    
    // ==================== UTILIDADES ====================
    
    /**
     * Limpia todas las preferencias de la aplicación
     * Útil para logout completo o reset de la app
     */
    public void clearAllPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
    
    /**
     * Obtiene el timestamp de la última receta como long
     * @return Timestamp en milisegundos o 0 si no hay última receta
     */
    public long getLastRecipeTimestamp() {
        return preferences.getLong(KEY_LAST_RECIPE_TIME, 0);
    }
    
    /**
     * Verifica si la última receta fue modificada en las últimas 24 horas
     * @return true si fue modificada recientemente
     */
    public boolean wasLastRecipeModifiedRecently() {
        long lastTime = getLastRecipeTimestamp();
        if (lastTime == 0) return false;
        
        long currentTime = System.currentTimeMillis();
        long twentyFourHours = 24 * 60 * 60 * 1000; // 24 horas en milisegundos
        
        return (currentTime - lastTime) < twentyFourHours;
    }
}
