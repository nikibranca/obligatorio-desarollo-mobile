package com.app.recetas.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Respuesta de la API para obtener todas las categorías disponibles
 * Endpoint: /categories.php
 */
public class CategoryResponse {
    
    /**
     * Lista de categorías disponibles en TheMealDB
     */
    @SerializedName("meals")
    public List<CategoryDto> meals;
    
    /**
     * DTO interno para cada categoría individual
     */
    public static class CategoryDto {
        
        // ID de la categoría
        @SerializedName("idCategory")
        public String idCategory;
        
        // Nombre de la categoría (ej: "Beef", "Chicken", "Dessert")
        @SerializedName("strCategory")
        public String strCategory;
        
        // Descripción de la categoría
        @SerializedName("strCategoryDescription")
        public String strCategoryDescription;
        
        // URL de imagen representativa de la categoría
        @SerializedName("strCategoryThumb")
        public String strCategoryThumb;
    }
}
