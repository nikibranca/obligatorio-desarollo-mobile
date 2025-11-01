package com.app.recetas.data.remote.api;

import com.app.recetas.data.remote.dto.AreaResponse;
import com.app.recetas.data.remote.dto.CategoryResponse;
import com.app.recetas.data.remote.dto.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interfaz que define los endpoints de TheMealDB API
 * Retrofit genera automáticamente la implementación de estos métodos
 * Base URL: https://www.themealdb.com/api/json/v1/1/
 */
public interface MealApiService {
    
    /**
     * Busca recetas por nombre
     * Endpoint: /search.php?s={nombre}
     * Ejemplo: /search.php?s=Arrabiata
     * @param name Nombre o parte del nombre de la receta a buscar
     * @return Call con MealResponse que contiene lista de recetas encontradas
     */
    @GET("search.php")
    Call<MealResponse> searchByName(@Query("s") String name);
    
    /**
     * Busca recetas por categoría
     * Endpoint: /filter.php?c={categoria}
     * Ejemplo: /filter.php?c=Seafood
     * @param category Categoría exacta (debe coincidir con las disponibles en la API)
     * @return Call con MealResponse que contiene recetas de esa categoría
     */
    @GET("filter.php")
    Call<MealResponse> searchByCategory(@Query("c") String category);
    
    /**
     * Busca recetas por área geográfica
     * Endpoint: /filter.php?a={area}
     * Ejemplo: /filter.php?a=Italian
     * @param area Área geográfica exacta (debe coincidir con las disponibles)
     * @return Call con MealResponse que contiene recetas de esa área
     */
    @GET("filter.php")
    Call<MealResponse> searchByArea(@Query("a") String area);
    
    /**
     * Obtiene todas las categorías disponibles
     * Endpoint: /categories.php
     * Usado para poblar el spinner de categorías en la búsqueda
     * @return Call con CategoryResponse que contiene todas las categorías
     */
    @GET("categories.php")
    Call<CategoryResponse> getCategories();
    
    /**
     * Obtiene todas las áreas geográficas disponibles
     * Endpoint: /list.php?a=list
     * Usado para poblar el spinner de áreas en la búsqueda
     * @return Call con AreaResponse que contiene todas las áreas
     */
    @GET("list.php?a=list")
    Call<AreaResponse> getAreas();
    
    /**
     * Obtiene los detalles completos de una receta por su ID
     * Endpoint: /lookup.php?i={id}
     * Útil cuando se necesitan todos los detalles de una receta específica
     * @param id ID único de la receta en TheMealDB
     * @return Call con MealResponse que contiene los detalles completos
     */
    @GET("lookup.php")
    Call<MealResponse> getRecipeById(@Query("i") String id);
    
    /**
     * Obtiene una receta aleatoria
     * Endpoint: /random.php
     * Funcionalidad extra para mostrar recetas aleatorias al usuario
     * @return Call con MealResponse que contiene una receta aleatoria
     */
    @GET("random.php")
    Call<MealResponse> getRandomRecipe();
}
