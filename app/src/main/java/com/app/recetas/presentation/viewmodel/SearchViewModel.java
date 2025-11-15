package com.app.recetas.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.app.recetas.data.remote.dto.AreaResponse;
import com.app.recetas.data.remote.dto.CategoryResponse;
import com.app.recetas.data.remote.dto.MealDto;
import com.app.recetas.data.remote.dto.MealResponse;
import com.app.recetas.data.repository.RecipeRepository;
import com.app.recetas.utils.PreferencesManager;
import com.app.recetas.utils.SearchType;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel para la pantalla de búsqueda (SearchFragment)
 * Maneja búsquedas en TheMealDB API y adición de recetas a la colección
 * Gestiona spinners de categorías y áreas geográficas
 */
public class SearchViewModel extends AndroidViewModel {
    
    // Repositorio para acceso a datos locales y remotos
    private RecipeRepository repository;
    
    // Manager para SharedPreferences
    private PreferencesManager preferencesManager;
    
    // LiveData para resultados de búsqueda
    private MutableLiveData<List<MealDto>> searchResults = new MutableLiveData<>();
    
    // LiveData para categorías disponibles (para spinner)
    private MutableLiveData<List<String>> categories = new MutableLiveData<>();
    
    // LiveData para áreas disponibles (para spinner)
    private MutableLiveData<List<String>> areas = new MutableLiveData<>();
    
    // LiveData para mostrar/ocultar loading
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // LiveData para mostrar mensajes al usuario
    private MutableLiveData<String> message = new MutableLiveData<>();
    
    // LiveData para controlar estado de error
    private MutableLiveData<String> error = new MutableLiveData<>();
    
    /**
     * Constructor del ViewModel
     * @param application Contexto de aplicación
     */
    public SearchViewModel(@NonNull Application application) {
        super(application);
        
        // Inicializar dependencias
        repository = new RecipeRepository(application);
        preferencesManager = new PreferencesManager(application);
        
        // Cargar datos iniciales
        loadCategories();
        loadAreas();
        
        // Inicializar con lista vacía
        searchResults.setValue(new ArrayList<>());
    }
    
    // ==================== GETTERS PARA LIVEDATA ====================
    
    /**
     * Obtiene LiveData con resultados de búsqueda
     * @return LiveData con lista de recetas encontradas en la API
     */
    public MutableLiveData<List<MealDto>> getSearchResults() {
        return searchResults;
    }
    
    /**
     * Obtiene LiveData con categorías para poblar spinner
     * @return LiveData con lista de nombres de categorías
     */
    public MutableLiveData<List<String>> getCategories() {
        return categories;
    }
    
    /**
     * Obtiene LiveData con áreas para poblar spinner
     * @return LiveData con lista de nombres de áreas
     */
    public MutableLiveData<List<String>> getAreas() {
        return areas;
    }
    
    /**
     * Obtiene LiveData para controlar indicador de carga
     * @return LiveData boolean para mostrar/ocultar loading
     */
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * Obtiene LiveData para mostrar mensajes informativos
     * @return LiveData con mensajes para el usuario
     */
    public MutableLiveData<String> getMessage() {
        return message;
    }
    
    /**
     * Obtiene LiveData para mostrar errores
     * @return LiveData con mensajes de error
     */
    public MutableLiveData<String> getError() {
        return error;
    }
    
    /**
     * Obtiene el repositorio (para verificaciones internas)
     * @return RecipeRepository
     */
    public RecipeRepository getRepository() {
        return repository;
    }
    
    // ==================== OPERACIONES DE BÚSQUEDA ====================
    
    /**
     * Realiza búsqueda de recetas según el tipo especificado
     * @param query Término de búsqueda
     * @param searchType Tipo de búsqueda (NAME, CATEGORY, AREA)
     */
    public void searchRecipes(String query, SearchType searchType) {
        // Validar entrada
        if (query == null || query.trim().isEmpty()) {
            error.setValue("Debe ingresar un término de búsqueda");
            return;
        }
        
        // Mostrar loading
        isLoading.setValue(true);
        error.setValue(""); // Limpiar errores previos
        
        // Crear call según tipo de búsqueda
        Call<MealResponse> call;
        switch (searchType) {
            case NAME:
                call = repository.searchRecipesByName(query.trim());
                break;
            case CATEGORY:
                call = repository.searchRecipesByCategory(query.trim());
                break;
            case AREA:
                call = repository.searchRecipesByArea(query.trim());
                break;
            default:
                call = repository.searchRecipesByName(query.trim());
                break;
        }
        
        // Ejecutar búsqueda de forma asíncrona
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call, @NonNull Response<MealResponse> response) {
                // Ocultar loading
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    MealResponse mealResponse = response.body();
                    
                    if (mealResponse.hasResults()) {
                        // Verificar si necesitamos obtener detalles completos
                        if (searchType == SearchType.CATEGORY || searchType == SearchType.AREA) {
                            // Para búsquedas por categoría/área, obtener detalles completos
                            fetchCompleteRecipeDetails(mealResponse.meals);
                        } else {
                            // Para búsqueda por nombre, usar directamente
                            searchResults.setValue(mealResponse.meals);
                            message.setValue("Se encontraron " + mealResponse.getResultCount() + " recetas");
                        }
                    } else {
                        // Búsqueda exitosa pero sin resultados
                        searchResults.setValue(new ArrayList<>());
                        message.setValue("No se encontraron recetas para: " + query);
                    }
                } else {
                    // Error en la respuesta del servidor
                    handleApiError("Error en el servidor. Código: " + response.code());
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                // Error de conexión o red
                isLoading.setValue(false);
                handleApiError("Error de conexión: " + t.getMessage());
            }
        });
    }
    
    /**
     * Obtiene una receta aleatoria de la API
     * Funcionalidad extra para sugerir recetas al usuario
     */
    public void getRandomRecipe() {
        isLoading.setValue(true);
        error.setValue("");
        
        repository.getRandomRecipe().enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call, @NonNull Response<MealResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null && response.body().hasResults()) {
                    // Mostrar la receta aleatoria como resultado único
                    List<MealDto> randomList = new ArrayList<>();
                    randomList.add(response.body().meals.get(0));
                    searchResults.setValue(randomList);
                    message.setValue("Receta aleatoria sugerida");
                } else {
                    handleApiError("No se pudo obtener receta aleatoria");
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                handleApiError("Error obteniendo receta aleatoria: " + t.getMessage());
            }
        });
    }
    
    /**
     * Obtiene los detalles completos para una lista de recetas básicas
     * @param basicRecipes Lista de recetas con información básica
     */
    private void fetchCompleteRecipeDetails(List<MealDto> basicRecipes) {
        if (basicRecipes == null || basicRecipes.isEmpty()) {
            searchResults.setValue(new ArrayList<>());
            return;
        }
        
        List<MealDto> completeRecipes = new ArrayList<>();
        final int totalRecipes = Math.min(basicRecipes.size(), 10); // Limitar a 10 para no sobrecargar
        
        // Contador para saber cuándo terminamos todas las llamadas
        final int[] completedCalls = {0};
        
        for (int i = 0; i < totalRecipes; i++) {
            MealDto basicRecipe = basicRecipes.get(i);
            
            repository.getRecipeById(basicRecipe.idMeal).enqueue(new Callback<MealResponse>() {
                @Override
                public void onResponse(@NonNull Call<MealResponse> call, @NonNull Response<MealResponse> response) {
                    synchronized (completeRecipes) {
                        if (response.isSuccessful() && response.body() != null && response.body().hasResults()) {
                            completeRecipes.add(response.body().meals.get(0));
                        } else {
                            // Si falla obtener detalles, usar la receta básica
                            completeRecipes.add(basicRecipe);
                        }
                        
                        completedCalls[0]++;
                        
                        // Cuando terminemos todas las llamadas, actualizar UI
                        if (completedCalls[0] == totalRecipes) {
                            isLoading.setValue(false);
                            searchResults.setValue(completeRecipes);
                            message.setValue("Se encontraron " + completeRecipes.size() + " recetas con detalles completos");
                        }
                    }
                }
                
                @Override
                public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                    synchronized (completeRecipes) {
                        // Si falla, usar la receta básica
                        completeRecipes.add(basicRecipe);
                        completedCalls[0]++;
                        
                        if (completedCalls[0] == totalRecipes) {
                            isLoading.setValue(false);
                            searchResults.setValue(completeRecipes);
                            message.setValue("Se encontraron " + completeRecipes.size() + " recetas");
                        }
                    }
                }
            });
        }
    }
    
    // ==================== GESTIÓN DE COLECCIÓN ====================
    
    /**
     * Agrega una receta de la API a la colección del usuario
     * @param mealDto Receta de la API a agregar
     */
    public void addToCollection(MealDto mealDto) {
        if (mealDto == null) {
            error.setValue("Error: Receta no válida");
            return;
        }
        
        // Mostrar loading
        isLoading.setValue(true);
        
        // Las recetas ya vienen completas desde la búsqueda
        addRecipeToCollection(mealDto);
    }
    
    /**
     * Obtiene los detalles completos de una receta por ID y la agrega a la colección
     * @param recipeId ID de la receta
     * @param recipeName Nombre de la receta (para mostrar en mensajes)
     */
    private void getCompleteRecipeAndAdd(String recipeId, String recipeName) {
        repository.getRecipeById(recipeId).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(@NonNull Call<MealResponse> call, @NonNull Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().hasResults()) {
                    // Obtener la receta completa
                    MealDto completeRecipe = response.body().meals.get(0);
                    addRecipeToCollection(completeRecipe);
                } else {
                    isLoading.setValue(false);
                    error.setValue("No se pudieron obtener los detalles completos de la receta");
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<MealResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue("Error obteniendo detalles de la receta: " + t.getMessage());
            }
        });
    }
    
    /**
     * Agrega una receta completa a la colección
     * @param mealDto Receta con información completa
     */
    private void addRecipeToCollection(MealDto mealDto) {
        // Ejecutar en hilo separado para no bloquear UI
        new Thread(() -> {
            try {
                // Convertir MealDto a Recipe
                com.app.recetas.data.local.entities.Recipe recipe = mealDto.toRecipe();
                
                // Insertar en base de datos local
                repository.insertRecipeSync(recipe);
                
                // Actualizar SharedPreferences con última receta agregada
                preferencesManager.saveLastRecipe(recipe.getId(), recipe.getName());
                
                // Mostrar mensaje de éxito en UI thread
                message.postValue("Receta '" + recipe.getName() + "' agregada a tu colección");
                
            } catch (Exception e) {
                // Manejar error
                error.postValue("Error agregando receta: " + e.getMessage());
            } finally {
                // Ocultar loading
                isLoading.postValue(false);
            }
        }).start();
    }
    
    // ==================== CARGA DE DATOS PARA SPINNERS ====================
    
    /**
     * Carga todas las categorías disponibles desde la API
     * Para poblar el spinner de categorías
     */
    private void loadCategories() {
        repository.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().meals != null) {
                    // Extraer nombres de categorías
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("Seleccionar categoría"); // Opción por defecto
                    
                    for (CategoryResponse.CategoryDto category : response.body().meals) {
                        if (category.strCategory != null && !category.strCategory.isEmpty()) {
                            categoryNames.add(category.strCategory);
                        }
                    }
                    
                    categories.setValue(categoryNames);
                } else {
                    // Error cargando categorías, usar lista por defecto
                    List<String> defaultCategories = getDefaultCategories();
                    categories.setValue(defaultCategories);
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                // Error de conexión, usar lista por defecto
                List<String> defaultCategories = getDefaultCategories();
                categories.setValue(defaultCategories);
            }
        });
    }
    
    /**
     * Carga todas las áreas disponibles desde la API
     * Para poblar el spinner de áreas
     */
    private void loadAreas() {
        repository.getAreas().enqueue(new Callback<AreaResponse>() {
            @Override
            public void onResponse(@NonNull Call<AreaResponse> call, @NonNull Response<AreaResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().meals != null) {
                    // Extraer nombres de áreas
                    List<String> areaNames = new ArrayList<>();
                    areaNames.add("Seleccionar área"); // Opción por defecto
                    
                    for (AreaResponse.AreaDto area : response.body().meals) {
                        if (area.strArea != null && !area.strArea.isEmpty()) {
                            areaNames.add(area.strArea);
                        }
                    }
                    
                    areas.setValue(areaNames);
                } else {
                    // Error cargando áreas, usar lista por defecto
                    List<String> defaultAreas = getDefaultAreas();
                    areas.setValue(defaultAreas);
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<AreaResponse> call, @NonNull Throwable t) {
                // Error de conexión, usar lista por defecto
                List<String> defaultAreas = getDefaultAreas();
                areas.setValue(defaultAreas);
            }
        });
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Maneja errores de la API de forma centralizada
     * @param errorMessage Mensaje de error a mostrar
     */
    private void handleApiError(String errorMessage) {
        error.setValue(errorMessage);
        searchResults.setValue(new ArrayList<>()); // Limpiar resultados
    }
    
    /**
     * Lista por defecto de categorías en caso de error de API
     * @return Lista con categorías básicas
     */
    private List<String> getDefaultCategories() {
        List<String> defaultList = new ArrayList<>();
        defaultList.add("Seleccionar categoría");
        defaultList.add("Beef");
        defaultList.add("Chicken");
        defaultList.add("Dessert");
        defaultList.add("Pasta");
        defaultList.add("Seafood");
        defaultList.add("Vegetarian");
        return defaultList;
    }
    
    /**
     * Lista por defecto de áreas en caso de error de API
     * @return Lista con áreas básicas
     */
    private List<String> getDefaultAreas() {
        List<String> defaultList = new ArrayList<>();
        defaultList.add("Seleccionar área");
        defaultList.add("American");
        defaultList.add("British");
        defaultList.add("Chinese");
        defaultList.add("French");
        defaultList.add("Italian");
        defaultList.add("Mexican");
        return defaultList;
    }
    
    /**
     * Limpia los resultados de búsqueda
     */
    public void clearSearchResults() {
        searchResults.setValue(new ArrayList<>());
        message.setValue("");
        error.setValue("");
    }
    
    // ==================== CLEANUP ====================
    
    /**
     * Limpia recursos cuando el ViewModel se destruye
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cleanup();
    }
}
