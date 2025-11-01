package com.app.recetas.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entidad Recipe que representa una receta en la base de datos local
 * Utiliza Room para el mapeo objeto-relacional
 */
@Entity(tableName = "recipes") // Define la tabla "recipes" en SQLite
public class Recipe {
    
    // Clave primaria de la tabla - ID único de la receta
    @PrimaryKey
    @NonNull
    public String id;
    
    // Nombre de la receta (ej: "Pasta Carbonara")
    public String name;
    
    // Categoría de la receta (ej: "Pasta", "Dessert", "Beef")
    public String category;
    
    // Área geográfica de origen (ej: "Italian", "Mexican", "Chinese")
    public String area;
    
    // Instrucciones paso a paso para preparar la receta
    public String instructions;
    
    // URL de la imagen de la receta
    public String imageUrl;
    
    // Ingredientes en formato JSON string (para almacenar lista compleja)
    public String ingredients;
    
    // Notas personales que el usuario puede agregar
    public String personalNotes;
    
    // Indica si es una receta creada por el usuario (true) o de API externa (false)
    public boolean isPersonal;
    
    // Timestamp de cuando se agregó la receta
    public long dateAdded;
    
    // Timestamp de la última modificación (para SharedPreferences)
    public long dateModified;
    
    /**
     * Constructor principal para crear una nueva receta
     * @param id ID único de la receta
     * @param name Nombre de la receta
     * @param category Categoría de la receta
     * @param area Área geográfica
     * @param instructions Instrucciones de preparación
     * @param imageUrl URL de la imagen
     * @param ingredients Ingredientes en formato JSON
     */
    public Recipe(@NonNull String id, String name, String category, String area, 
                  String instructions, String imageUrl, String ingredients) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.area = area;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        
        // Valores por defecto
        this.personalNotes = ""; // Sin notas inicialmente
        this.isPersonal = false; // Por defecto viene de API externa
        this.dateAdded = System.currentTimeMillis(); // Timestamp actual
        this.dateModified = System.currentTimeMillis(); // Timestamp actual
    }
    
    // Constructor vacío requerido por Room
    public Recipe() {}
    
    // Getters y Setters para acceso a los campos
    
    @NonNull
    public String getId() {
        return id;
    }
    
    public void setId(@NonNull String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getArea() {
        return area;
    }
    
    public void setArea(String area) {
        this.area = area;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    
    public String getPersonalNotes() {
        return personalNotes;
    }
    
    public void setPersonalNotes(String personalNotes) {
        this.personalNotes = personalNotes;
        // Actualizar timestamp de modificación cuando se cambian las notas
        this.dateModified = System.currentTimeMillis();
    }
    
    public boolean isPersonal() {
        return isPersonal;
    }
    
    public void setPersonal(boolean personal) {
        isPersonal = personal;
    }
    
    public long getDateAdded() {
        return dateAdded;
    }
    
    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
    
    public long getDateModified() {
        return dateModified;
    }
    
    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }
}
