package com.app.recetas.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Clase que mapea la respuesta JSON de TheMealDB para búsquedas de recetas
 * La API retorna un objeto con una propiedad "meals" que contiene un array de recetas
 */
public class MealResponse {
    
    /**
     * Lista de recetas retornadas por la API
     * Puede ser null si no se encontraron resultados
     */
    @SerializedName("meals")
    public List<MealDto> meals;
    
    /**
     * Constructor vacío requerido por Gson para deserialización
     */
    public MealResponse() {}
    
    /**
     * Verifica si la respuesta contiene recetas
     * @return true si hay recetas, false si la lista está vacía o es null
     */
    public boolean hasResults() {
        return meals != null && !meals.isEmpty();
    }
    
    /**
     * Obtiene el número de recetas en la respuesta
     * @return Cantidad de recetas, 0 si no hay resultados
     */
    public int getResultCount() {
        return meals != null ? meals.size() : 0;
    }
}
