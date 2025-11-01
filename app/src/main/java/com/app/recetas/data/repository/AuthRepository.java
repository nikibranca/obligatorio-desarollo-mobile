package com.app.recetas.data.repository;

import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

/**
 * AuthRepository temporal para testing sin Firebase
 * Simula autenticación para probar la funcionalidad básica
 */
public class AuthRepository {
    
    // Simulación de usuario logueado
    private String currentUserEmail = null;
    private String currentUserId = null;
    private boolean isLoggedIn = false;
    
    public AuthRepository() {
        // Constructor vacío para testing
    }
    
    /**
     * Simula login con validación básica
     */
    public void login(String email, String password, OnCompleteListener<AuthResult> listener) {
        // Validar parámetros
        if (email == null || email.trim().isEmpty()) {
            simulateFailure(listener, "Email no puede estar vacío");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            simulateFailure(listener, "Contraseña no puede estar vacía");
            return;
        }
        
        // Simular delay de red
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Simular login exitoso
            currentUserEmail = email.trim();
            currentUserId = "test_user_" + System.currentTimeMillis();
            isLoggedIn = true;
            
            simulateSuccess(listener);
        }, 1000);
    }
    
    /**
     * Simula registro con validación básica
     */
    public void register(String email, String password, OnCompleteListener<AuthResult> listener) {
        // Validar parámetros
        if (email == null || email.trim().isEmpty()) {
            simulateFailure(listener, "Email no puede estar vacío");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            simulateFailure(listener, "Contraseña no puede estar vacía");
            return;
        }
        if (password.length() < 6) {
            simulateFailure(listener, "Contraseña debe tener al menos 6 caracteres");
            return;
        }
        
        // Simular delay de red
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Simular registro exitoso
            currentUserEmail = email.trim();
            currentUserId = "new_user_" + System.currentTimeMillis();
            isLoggedIn = true;
            
            simulateSuccess(listener);
        }, 1500);
    }
    
    /**
     * Simula logout
     */
    public void logout() {
        currentUserEmail = null;
        currentUserId = null;
        isLoggedIn = false;
    }
    
    /**
     * Retorna null porque no usamos Firebase real
     */
    public FirebaseUser getCurrentUser() {
        return null; // No hay FirebaseUser en modo testing
    }
    
    /**
     * Verifica si hay usuario logueado (simulado)
     */
    public boolean isUserLoggedIn() {
        return isLoggedIn;
    }
    
    /**
     * Obtiene email del usuario simulado
     */
    public String getCurrentUserEmail() {
        return currentUserEmail;
    }
    
    /**
     * Obtiene ID del usuario simulado
     */
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    // ==================== MÉTODOS AUXILIARES PARA SIMULACIÓN ====================
    
    /**
     * Simula tarea exitosa
     */
    private void simulateSuccess(OnCompleteListener<AuthResult> listener) {
        Task<AuthResult> mockTask = new Task<AuthResult>() {
            @Override
            public boolean isComplete() { return true; }
            
            @Override
            public boolean isSuccessful() { return true; }
            
            @Override
            public boolean isCanceled() { return false; }
            
            @Override
            public AuthResult getResult() { return null; }
            
            @Override
            public <X extends Throwable> AuthResult getResult(Class<X> aClass) { return null; }
            
            @Override
            public Exception getException() { return null; }
            
            @Override
            public Task<AuthResult> addOnSuccessListener(com.google.android.gms.tasks.OnSuccessListener<? super AuthResult> onSuccessListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnSuccessListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnSuccessListener<? super AuthResult> onSuccessListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnSuccessListener(android.app.Activity activity, com.google.android.gms.tasks.OnSuccessListener<? super AuthResult> onSuccessListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnFailureListener(com.google.android.gms.tasks.OnFailureListener onFailureListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnFailureListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnFailureListener onFailureListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnFailureListener(android.app.Activity activity, com.google.android.gms.tasks.OnFailureListener onFailureListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnCompleteListener(OnCompleteListener<AuthResult> onCompleteListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnCompleteListener(java.util.concurrent.Executor executor, OnCompleteListener<AuthResult> onCompleteListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnCompleteListener(android.app.Activity activity, OnCompleteListener<AuthResult> onCompleteListener) { return this; }
        };
        
        listener.onComplete(mockTask);
    }
    
    /**
     * Simula tarea fallida
     */
    private void simulateFailure(OnCompleteListener<AuthResult> listener, String errorMessage) {
        Task<AuthResult> mockTask = new Task<AuthResult>() {
            @Override
            public boolean isComplete() { return true; }
            
            @Override
            public boolean isSuccessful() { return false; }
            
            @Override
            public boolean isCanceled() { return false; }
            
            @Override
            public AuthResult getResult() { return null; }
            
            @Override
            public <X extends Throwable> AuthResult getResult(Class<X> aClass) { return null; }
            
            @Override
            public Exception getException() { return new Exception(errorMessage); }
            
            @Override
            public Task<AuthResult> addOnSuccessListener(com.google.android.gms.tasks.OnSuccessListener<? super AuthResult> onSuccessListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnSuccessListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnSuccessListener<? super AuthResult> onSuccessListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnSuccessListener(android.app.Activity activity, com.google.android.gms.tasks.OnSuccessListener<? super AuthResult> onSuccessListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnFailureListener(com.google.android.gms.tasks.OnFailureListener onFailureListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnFailureListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnFailureListener onFailureListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnFailureListener(android.app.Activity activity, com.google.android.gms.tasks.OnFailureListener onFailureListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnCompleteListener(OnCompleteListener<AuthResult> onCompleteListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnCompleteListener(java.util.concurrent.Executor executor, OnCompleteListener<AuthResult> onCompleteListener) { return this; }
            
            @Override
            public Task<AuthResult> addOnCompleteListener(android.app.Activity activity, OnCompleteListener<AuthResult> onCompleteListener) { return this; }
        };
        
        listener.onComplete(mockTask);
    }
}
