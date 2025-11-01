package com.app.recetas.data.remote.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

/**
 * Cliente singleton para configurar Retrofit y realizar llamadas a TheMealDB API
 * Implementa patrón Singleton para reutilizar la misma instancia en toda la app
 */
public class ApiClient {
    
    // URL base de TheMealDB API
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    
    // Instancia singleton de Retrofit
    private static Retrofit retrofit = null;
    
    // Instancia singleton del servicio API
    private static MealApiService apiService = null;
    
    /**
     * Obtiene la instancia configurada de Retrofit
     * Si no existe, la crea con todas las configuraciones necesarias
     * @return Instancia configurada de Retrofit
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Crear interceptor para logging (útil para debug)
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log completo de requests/responses
            
            // Configurar cliente HTTP con timeouts y logging
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Agregar logging
                .connectTimeout(30, TimeUnit.SECONDS) // Timeout de conexión: 30 segundos
                .readTimeout(30, TimeUnit.SECONDS) // Timeout de lectura: 30 segundos
                .writeTimeout(30, TimeUnit.SECONDS) // Timeout de escritura: 30 segundos
                .build();
            
            // Crear instancia de Retrofit con configuraciones
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // URL base de la API
                .client(okHttpClient) // Cliente HTTP configurado
                .addConverterFactory(GsonConverterFactory.create()) // Convertidor JSON con Gson
                .build();
        }
        return retrofit;
    }
    
    /**
     * Obtiene la instancia del servicio API
     * Crea el servicio usando Retrofit si no existe
     * @return Instancia de MealApiService lista para usar
     */
    public static MealApiService getApiService() {
        if (apiService == null) {
            apiService = getClient().create(MealApiService.class);
        }
        return apiService;
    }
    
    /**
     * Método para limpiar las instancias (útil para testing)
     * Resetea las instancias singleton
     */
    public static void clearInstances() {
        retrofit = null;
        apiService = null;
    }
}
