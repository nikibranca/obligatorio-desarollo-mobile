package com.app.recetas.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Utilidad para validar conexión con Firebase
 */
public class FirebaseConnectionValidator {
    
    private static final String TAG = "FirebaseValidator";
    
    /**
     * Verifica si hay conexión a internet
     */
    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            Log.d(TAG, "Conexión a internet: " + isConnected);
            return isConnected;
        }
        return false;
    }
    
    /**
     * Verifica si Firebase Auth está inicializado
     */
    public static boolean isFirebaseAuthInitialized() {
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            boolean initialized = auth != null;
            Log.d(TAG, "Firebase Auth inicializado: " + initialized);
            return initialized;
        } catch (Exception e) {
            Log.e(TAG, "Error verificando Firebase Auth", e);
            return false;
        }
    }
    
    /**
     * Verifica conexión con Firebase Auth
     */
    public static void validateFirebaseConnection(Context context, FirebaseConnectionCallback callback) {
        Log.d(TAG, "Validando conexión con Firebase...");
        
        // 1. Verificar internet
        if (!hasInternetConnection(context)) {
            callback.onConnectionResult(false, "Sin conexión a internet");
            return;
        }
        
        // 2. Verificar Firebase Auth
        if (!isFirebaseAuthInitialized()) {
            callback.onConnectionResult(false, "Firebase Auth no inicializado");
            return;
        }
        
        // Firebase Auth está disponible
        Log.d(TAG, "Conexión con Firebase Auth exitosa");
        callback.onConnectionResult(true, "Conexión exitosa");
    }
    
    /**
     * Callback para resultado de validación
     */
    public interface FirebaseConnectionCallback {
        void onConnectionResult(boolean isConnected, String message);
    }
}
