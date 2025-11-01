package com.app.recetas.utils;

/**
 * Enum que define los tipos de búsqueda disponibles en TheMealDB API
 * Usado para determinar qué endpoint llamar según la selección del usuario
 */
public enum SearchType {
    
    /**
     * Búsqueda por nombre de receta
     * Endpoint: /search.php?s={nombre}
     * Permite búsqueda parcial del nombre
     */
    NAME("Nombre"),
    
    /**
     * Búsqueda por categoría
     * Endpoint: /filter.php?c={categoria}
     * Debe coincidir exactamente con categorías disponibles
     */
    CATEGORY("Categoría"),
    
    /**
     * Búsqueda por área geográfica
     * Endpoint: /filter.php?a={area}
     * Debe coincidir exactamente con áreas disponibles
     */
    AREA("Área");
    
    // Nombre descriptivo para mostrar en la UI
    private final String displayName;
    
    /**
     * Constructor del enum
     * @param displayName Nombre para mostrar al usuario
     */
    SearchType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Obtiene el nombre para mostrar en la interfaz
     * @return Nombre descriptivo del tipo de búsqueda
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Convierte string a SearchType
     * Útil para procesar selecciones de spinner
     * @param displayName Nombre mostrado en UI
     * @return SearchType correspondiente o NAME por defecto
     */
    public static SearchType fromDisplayName(String displayName) {
        for (SearchType type : SearchType.values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        return NAME; // Por defecto buscar por nombre
    }
}
