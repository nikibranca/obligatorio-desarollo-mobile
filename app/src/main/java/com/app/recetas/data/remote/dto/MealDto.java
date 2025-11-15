package com.app.recetas.data.remote.dto;

import com.app.recetas.data.local.entities.Recipe;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * DTO (Data Transfer Object) para recibir datos de TheMealDB API
 * Mapea la respuesta JSON a objetos Java usando Gson
 */
public class MealDto {
    
    // ID único de la receta en TheMealDB
    @SerializedName("idMeal")
    public String idMeal;
    
    // Nombre de la receta
    @SerializedName("strMeal")
    public String strMeal;
    
    // Categoría de la receta (Beef, Chicken, Dessert, etc.)
    @SerializedName("strCategory")
    public String strCategory;
    
    // Área geográfica (Italian, Mexican, British, etc.)
    @SerializedName("strArea")
    public String strArea;
    
    // Instrucciones completas de preparación
    @SerializedName("strInstructions")
    public String strInstructions;
    
    // URL de la imagen de la receta
    @SerializedName("strMealThumb")
    public String strMealThumb;
    
    // TheMealDB proporciona hasta 20 ingredientes y 20 medidas
    // Mapeamos todos para poder procesarlos
    @SerializedName("strIngredient1") public String strIngredient1;
    @SerializedName("strIngredient2") public String strIngredient2;
    @SerializedName("strIngredient3") public String strIngredient3;
    @SerializedName("strIngredient4") public String strIngredient4;
    @SerializedName("strIngredient5") public String strIngredient5;
    @SerializedName("strIngredient6") public String strIngredient6;
    @SerializedName("strIngredient7") public String strIngredient7;
    @SerializedName("strIngredient8") public String strIngredient8;
    @SerializedName("strIngredient9") public String strIngredient9;
    @SerializedName("strIngredient10") public String strIngredient10;
    @SerializedName("strIngredient11") public String strIngredient11;
    @SerializedName("strIngredient12") public String strIngredient12;
    @SerializedName("strIngredient13") public String strIngredient13;
    @SerializedName("strIngredient14") public String strIngredient14;
    @SerializedName("strIngredient15") public String strIngredient15;
    @SerializedName("strIngredient16") public String strIngredient16;
    @SerializedName("strIngredient17") public String strIngredient17;
    @SerializedName("strIngredient18") public String strIngredient18;
    @SerializedName("strIngredient19") public String strIngredient19;
    @SerializedName("strIngredient20") public String strIngredient20;
    
    // Medidas correspondientes a cada ingrediente
    @SerializedName("strMeasure1") public String strMeasure1;
    @SerializedName("strMeasure2") public String strMeasure2;
    @SerializedName("strMeasure3") public String strMeasure3;
    @SerializedName("strMeasure4") public String strMeasure4;
    @SerializedName("strMeasure5") public String strMeasure5;
    @SerializedName("strMeasure6") public String strMeasure6;
    @SerializedName("strMeasure7") public String strMeasure7;
    @SerializedName("strMeasure8") public String strMeasure8;
    @SerializedName("strMeasure9") public String strMeasure9;
    @SerializedName("strMeasure10") public String strMeasure10;
    @SerializedName("strMeasure11") public String strMeasure11;
    @SerializedName("strMeasure12") public String strMeasure12;
    @SerializedName("strMeasure13") public String strMeasure13;
    @SerializedName("strMeasure14") public String strMeasure14;
    @SerializedName("strMeasure15") public String strMeasure15;
    @SerializedName("strMeasure16") public String strMeasure16;
    @SerializedName("strMeasure17") public String strMeasure17;
    @SerializedName("strMeasure18") public String strMeasure18;
    @SerializedName("strMeasure19") public String strMeasure19;
    @SerializedName("strMeasure20") public String strMeasure20;
    
    /**
     * Convierte este MealDto a una entidad Recipe para almacenar en Room
     * @return Recipe lista para insertar en la base de datos local
     */
    public Recipe toRecipe() {
        return new Recipe(
            idMeal != null ? idMeal : "", // ID de la receta
            strMeal != null ? strMeal : "", // Nombre
            strCategory != null ? strCategory : "Sin categoría", // Categoría
            strArea != null ? strArea : "Sin área", // Área
            strInstructions != null ? strInstructions : "Instrucciones no disponibles. Busca por nombre para obtener detalles completos.", // Instrucciones
            strMealThumb != null ? strMealThumb : "", // URL imagen
            buildIngredientsJson() // Ingredientes en formato JSON
        );
    }
    
    /**
     * Construye un JSON string con todos los ingredientes y medidas
     * Filtra ingredientes vacíos o nulos
     * MÉTODO PÚBLICO para usar desde otras clases
     * @return String JSON con formato: [{"ingredient":"Chicken","measure":"1 whole"}]
     */
    public String buildIngredientsJson() {
        JSONArray ingredientsArray = new JSONArray();
        
        // Array con todos los ingredientes para iterar fácilmente
        String[] ingredients = {
            strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
            strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
            strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
            strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
        };
        
        // Array con todas las medidas correspondientes
        String[] measures = {
            strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
            strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
            strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
            strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
        };
        
        // Verificar si todos los ingredientes son nulos (búsqueda por categoría/área)
        boolean hasAnyIngredient = false;
        for (String ingredient : ingredients) {
            if (ingredient != null && !ingredient.trim().isEmpty()) {
                hasAnyIngredient = true;
                break;
            }
        }
        
        // Si no hay ingredientes, agregar mensaje informativo
        if (!hasAnyIngredient) {
            try {
                JSONObject infoObj = new JSONObject();
                infoObj.put("ingredient", "Ingredientes no disponibles");
                infoObj.put("measure", "Busca por nombre para obtener la lista completa de ingredientes");
                ingredientsArray.put(infoObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ingredientsArray.toString();
        }
        
        // Procesar cada ingrediente y su medida correspondiente
        for (int i = 0; i < ingredients.length; i++) {
            String ingredient = ingredients[i];
            String measure = measures[i];
            
            // Solo agregar si el ingrediente no está vacío
            if (ingredient != null && !ingredient.trim().isEmpty()) {
                try {
                    JSONObject ingredientObj = new JSONObject();
                    ingredientObj.put("ingredient", ingredient.trim());
                    // Si no hay medida, usar string vacío
                    ingredientObj.put("measure", measure != null ? measure.trim() : "");
                    ingredientsArray.put(ingredientObj);
                } catch (JSONException e) {
                    // Si hay error creando JSON, continuar con el siguiente ingrediente
                    e.printStackTrace();
                }
            }
        }
        
        // Retornar el JSON como string, o array vacío si hay error
        return ingredientsArray.toString();
    }
}
