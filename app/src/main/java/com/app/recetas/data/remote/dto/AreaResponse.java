package com.app.recetas.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Respuesta de la API para obtener todas las áreas geográficas disponibles
 * Endpoint: /list.php?a=list
 */
public class AreaResponse {
    
    /**
     * Lista de áreas geográficas disponibles en TheMealDB
     */
    @SerializedName("meals")
    public List<AreaDto> meals;
    
    /**
     * DTO interno para cada área geográfica individual
     */
    public static class AreaDto {
        
        // Nombre del área geográfica (ej: "Italian", "Mexican", "Chinese")
        @SerializedName("strArea")
        public String strArea;
    }
}
