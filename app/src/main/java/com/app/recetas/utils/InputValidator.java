package com.app.recetas.utils;

import android.util.Patterns;

/**
 * Clase utilitaria para validar datos de entrada en formularios
 * Implementa todas las validaciones requeridas por el obligatorio
 * Valida formato de datos y campos vacíos
 */
public class InputValidator {
    
    /**
     * Valida formato y contenido de email
     * @param email Email a validar
     * @return ValidationResult con resultado de la validación
     */
    public static ValidationResult validateEmail(String email) {
        // Verificar si el email está vacío o es null
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "El email no puede estar vacío");
        }
        
        // Verificar formato usando patrón de Android
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return new ValidationResult(false, "El formato del email no es válido");
        }
        
        // Email válido
        return new ValidationResult(true, "");
    }
    
    /**
     * Valida contraseña según criterios de seguridad
     * @param password Contraseña a validar
     * @return ValidationResult con resultado de la validación
     */
    public static ValidationResult validatePassword(String password) {
        // Verificar si la contraseña está vacía o es null
        if (password == null || password.trim().isEmpty()) {
            return new ValidationResult(false, "La contraseña no puede estar vacía");
        }
        
        // Verificar longitud mínima (Firebase requiere mínimo 6 caracteres)
        if (password.length() < 6) {
            return new ValidationResult(false, "La contraseña debe tener al menos 6 caracteres");
        }
        
        // Contraseña válida
        return new ValidationResult(true, "");
    }
    
    /**
     * Valida nombre de receta personalizada
     * @param name Nombre de la receta a validar
     * @return ValidationResult con resultado de la validación
     */
    public static ValidationResult validateRecipeName(String name) {
        // Verificar si el nombre está vacío o es null
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "El nombre de la receta no puede estar vacío");
        }
        
        // Verificar longitud mínima
        if (name.trim().length() < 3) {
            return new ValidationResult(false, "El nombre debe tener al menos 3 caracteres");
        }
        
        // Verificar longitud máxima
        if (name.trim().length() > 100) {
            return new ValidationResult(false, "El nombre no puede tener más de 100 caracteres");
        }
        
        // Nombre válido
        return new ValidationResult(true, "");
    }
    
    /**
     * Valida instrucciones de receta personalizada
     * @param instructions Instrucciones a validar
     * @return ValidationResult con resultado de la validación
     */
    public static ValidationResult validateInstructions(String instructions) {
        // Verificar si las instrucciones están vacías o son null
        if (instructions == null || instructions.trim().isEmpty()) {
            return new ValidationResult(false, "Las instrucciones no pueden estar vacías");
        }
        
        // Verificar longitud mínima
        if (instructions.trim().length() < 10) {
            return new ValidationResult(false, "Las instrucciones deben tener al menos 10 caracteres");
        }
        
        // Instrucciones válidas
        return new ValidationResult(true, "");
    }
    
    /**
     * Valida ingredientes de receta personalizada
     * @param ingredients Ingredientes a validar
     * @return ValidationResult con resultado de la validación
     */
    public static ValidationResult validateIngredients(String ingredients) {
        // Verificar si los ingredientes están vacíos o son null
        if (ingredients == null || ingredients.trim().isEmpty()) {
            return new ValidationResult(false, "Los ingredientes no pueden estar vacíos");
        }
        
        // Verificar longitud mínima
        if (ingredients.trim().length() < 5) {
            return new ValidationResult(false, "Debe especificar al menos un ingrediente");
        }
        
        // Ingredientes válidos
        return new ValidationResult(true, "");
    }
    
    /**
     * Valida categoría seleccionada
     * @param category Categoría a validar
     * @return ValidationResult con resultado de la validación
     */
    public static ValidationResult validateCategory(String category) {
        // Verificar si la categoría está vacía o es null
        if (category == null || category.trim().isEmpty()) {
            return new ValidationResult(false, "Debe seleccionar una categoría");
        }
        
        // Verificar que no sea el valor por defecto del spinner
        if (category.equals("Seleccionar categoría") || category.equals("Select category")) {
            return new ValidationResult(false, "Debe seleccionar una categoría válida");
        }
        
        // Categoría válida
        return new ValidationResult(true, "");
    }
    
    /**
     * Valida área geográfica seleccionada
     * @param area Área a validar
     * @return ValidationResult con resultado de la validación
     */
    public static ValidationResult validateArea(String area) {
        // Verificar si el área está vacía o es null
        if (area == null || area.trim().isEmpty()) {
            return new ValidationResult(false, "Debe seleccionar un área");
        }
        
        // Verificar que no sea el valor por defecto del spinner
        if (area.equals("Seleccionar área") || area.equals("Select area")) {
            return new ValidationResult(false, "Debe seleccionar un área válida");
        }
        
        // Área válida
        return new ValidationResult(true, "");
    }
    
    /**
     * Valida término de búsqueda
     * @param searchTerm Término de búsqueda a validar
     * @return ValidationResult con resultado de la validación
     */
    public static ValidationResult validateSearchTerm(String searchTerm) {
        // Verificar si el término está vacío o es null
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ValidationResult(false, "Ingrese un término de búsqueda");
        }
        
        // Verificar longitud mínima
        if (searchTerm.trim().length() < 2) {
            return new ValidationResult(false, "El término de búsqueda debe tener al menos 2 caracteres");
        }
        
        // Término válido
        return new ValidationResult(true, "");
    }
    
    /**
     * Valida notas personales (opcional, pero si se ingresa debe ser válida)
     * @param notes Notas a validar
     * @return ValidationResult con resultado de la validación
     */
    public static ValidationResult validatePersonalNotes(String notes) {
        // Las notas son opcionales, null o vacío es válido
        if (notes == null || notes.trim().isEmpty()) {
            return new ValidationResult(true, "");
        }
        
        // Si se ingresan notas, verificar longitud máxima
        if (notes.length() > 500) {
            return new ValidationResult(false, "Las notas no pueden tener más de 500 caracteres");
        }
        
        // Notas válidas
        return new ValidationResult(true, "");
    }
    
    /**
     * Clase interna para encapsular el resultado de una validación
     * Contiene si es válido y el mensaje de error si corresponde
     */
    public static class ValidationResult {
        // Indica si la validación fue exitosa
        public boolean isValid;
        
        // Mensaje de error si la validación falló (vacío si es válida)
        public String errorMessage;
        
        /**
         * Constructor del resultado de validación
         * @param isValid true si la validación fue exitosa
         * @param errorMessage mensaje de error (vacío si es válida)
         */
        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }
        
        /**
         * Verifica si la validación falló
         * @return true si hay error
         */
        public boolean hasError() {
            return !isValid;
        }
    }
}
