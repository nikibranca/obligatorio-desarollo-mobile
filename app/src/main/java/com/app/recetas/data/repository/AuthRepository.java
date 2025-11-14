package com.app.recetas.data.repository;

import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthException;

/**
 * AuthRepository con Firebase Authentication real
 * Maneja autenticación, validación de conexión y sesiones
 */
public class AuthRepository {
    
    private static final String TAG = "AuthRepository";
    private final FirebaseAuth firebaseAuth;
    
    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
    }
    
    /**
     * Login con Firebase Authentication
     */
    public void login(String email, String password, OnCompleteListener<AuthResult> listener) {
        if (!validateLoginInput(email, password)) {
            // Crear task fallido para inputs inválidos
            simulateFailedTask(listener, "Datos de entrada inválidos");
            return;
        }
        
        Log.d(TAG, "Iniciando login para: " + email);
        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Login exitoso");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "Usuario autenticado: " + user.getEmail());
                        }
                    } else {
                        Log.e(TAG, "Error en login", task.getException());
                    }
                    listener.onComplete(task);
                });
    }
    
    /**
     * Registro con Firebase Authentication
     */
    public void register(String email, String password, OnCompleteListener<AuthResult> listener) {
        if (!validateRegisterInput(email, password)) {
            // Crear task fallido para inputs inválidos
            simulateFailedTask(listener, "Datos de entrada inválidos");
            return;
        }
        
        Log.d(TAG, "Iniciando registro para: " + email);
        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Registro exitoso");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "Usuario registrado: " + user.getEmail());
                        }
                    } else {
                        Log.e(TAG, "Error en registro", task.getException());
                    }
                    listener.onComplete(task);
                });
    }
    
    /**
     * Logout de Firebase
     */
    public void logout() {
        Log.d(TAG, "Cerrando sesión");
        firebaseAuth.signOut();
    }
    
    /**
     * Obtiene el usuario actual de Firebase
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
    
    /**
     * Verifica si hay usuario logueado
     */
    public boolean isUserLoggedIn() {
        FirebaseUser user = getCurrentUser();
        boolean isLoggedIn = user != null;
        Log.d(TAG, "Usuario logueado: " + isLoggedIn);
        return isLoggedIn;
    }
    
    /**
     * Obtiene email del usuario actual
     */
    public String getCurrentUserEmail() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }
    
    /**
     * Obtiene ID del usuario actual
     */
    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    /**
     * Verifica si el usuario está verificado por email
     */
    public boolean isEmailVerified() {
        FirebaseUser user = getCurrentUser();
        return user != null && user.isEmailVerified();
    }
    
    /**
     * Envía email de verificación
     */
    public void sendEmailVerification(OnCompleteListener<Void> listener) {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            Log.d(TAG, "Enviando email de verificación");
            user.sendEmailVerification().addOnCompleteListener(listener);
        }
    }
    
    /**
     * Reautentica al usuario actual
     */
    public void reauthenticateUser(String password, OnCompleteListener<Void> listener) {
        FirebaseUser user = getCurrentUser();
        if (user != null && user.getEmail() != null) {
            Log.d(TAG, "Reautenticando usuario");
            com.google.firebase.auth.AuthCredential credential = 
                com.google.firebase.auth.EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(listener);
        }
    }
    
    /**
     * Obtiene mensaje de error legible
     */
    public String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException) exception;
            String errorCode = authException.getErrorCode();
            
            switch (errorCode) {
                case "ERROR_INVALID_EMAIL":
                    return "El formato del email no es válido";
                case "ERROR_WRONG_PASSWORD":
                    return "Contraseña incorrecta";
                case "ERROR_USER_NOT_FOUND":
                    return "No existe una cuenta con este email";
                case "ERROR_USER_DISABLED":
                    return "Esta cuenta ha sido deshabilitada";
                case "ERROR_TOO_MANY_REQUESTS":
                    return "Demasiados intentos fallidos. Intenta más tarde";
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "Ya existe una cuenta con este email";
                case "ERROR_WEAK_PASSWORD":
                    return "La contraseña es muy débil";
                case "ERROR_NETWORK_REQUEST_FAILED":
                    return "Error de conexión. Verifica tu internet";
                default:
                    return "Error de autenticación: " + authException.getMessage();
            }
        }
        return exception != null ? exception.getMessage() : "Error desconocido";
    }
    
    // ==================== VALIDACIONES PRIVADAS ====================
    
    private boolean validateLoginInput(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            Log.w(TAG, "Email vacío en login");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            Log.w(TAG, "Contraseña vacía en login");
            return false;
        }
        return true;
    }
    
    private boolean validateRegisterInput(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            Log.w(TAG, "Email vacío en registro");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            Log.w(TAG, "Contraseña vacía en registro");
            return false;
        }
        if (password.length() < 6) {
            Log.w(TAG, "Contraseña muy corta en registro");
            return false;
        }
        return true;
    }
    
    /**
     * Método de debug para verificar estado de Firebase
     */
    public void debugFirebaseState() {
        Log.d(TAG, "=== DEBUG FIREBASE STATE ===");
        Log.d(TAG, "Firebase App: " + com.google.firebase.FirebaseApp.getInstance().getName());
        Log.d(TAG, "Firebase Auth: " + firebaseAuth.toString());
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Usuario actual: " + currentUser.getEmail());
            Log.d(TAG, "UID: " + currentUser.getUid());
            Log.d(TAG, "Email verificado: " + currentUser.isEmailVerified());
        } else {
            Log.d(TAG, "No hay usuario actual");
        }
        Log.d(TAG, "=== END DEBUG ===");
    }
    
    /**
     * Simula un task fallido para validaciones
     */
    private void simulateFailedTask(OnCompleteListener<AuthResult> listener, String errorMessage) {
        com.google.android.gms.tasks.Task<AuthResult> failedTask = com.google.android.gms.tasks.Tasks.forException(new Exception(errorMessage));
        listener.onComplete(failedTask);
    }
}
