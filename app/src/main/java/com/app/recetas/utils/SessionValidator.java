package com.app.recetas.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.app.recetas.data.repository.AuthRepository;
import com.app.recetas.presentation.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseUser;

/**
 * Utilidad para validar sesiones de usuario
 */
public class SessionValidator {
    
    private static final String TAG = "SessionValidator";
    private static final long SESSION_TIMEOUT = 24 * 60 * 60 * 1000; // 24 horas
    
    private final AuthRepository authRepository;
    private final PreferencesManager preferencesManager;
    
    public SessionValidator(Context context) {
        this.authRepository = new AuthRepository();
        this.preferencesManager = new PreferencesManager(context);
    }
    
    /**
     * Valida si la sesión actual es válida
     */
    public SessionValidationResult validateCurrentSession() {
        Log.d(TAG, "Validando sesión actual...");
        
        // 1. Verificar si hay usuario en Firebase
        FirebaseUser firebaseUser = authRepository.getCurrentUser();
        if (firebaseUser == null) {
            Log.w(TAG, "No hay usuario autenticado en Firebase");
            return new SessionValidationResult(false, "No hay usuario autenticado", SessionValidationResult.Reason.NO_USER);
        }
        
        // 2. Verificar si el email está verificado (opcional, según tus requerimientos)
        if (!authRepository.isEmailVerified()) {
            Log.w(TAG, "Email no verificado");
            // Puedes decidir si esto invalida la sesión o no
            // return new SessionValidationResult(false, "Email no verificado", SessionValidationResult.Reason.EMAIL_NOT_VERIFIED);
        }
        
        // 3. Verificar timeout de sesión
        long lastActivity = preferencesManager.getLastActivityTime();
        long currentTime = System.currentTimeMillis();
        
        if (lastActivity > 0 && (currentTime - lastActivity) > SESSION_TIMEOUT) {
            Log.w(TAG, "Sesión expirada por timeout");
            return new SessionValidationResult(false, "Sesión expirada", SessionValidationResult.Reason.SESSION_EXPIRED);
        }
        
        // 4. Actualizar última actividad
        preferencesManager.updateLastActivityTime();
        
        Log.d(TAG, "Sesión válida para usuario: " + firebaseUser.getEmail());
        return new SessionValidationResult(true, "Sesión válida", SessionValidationResult.Reason.VALID);
    }
    
    /**
     * Valida sesión y redirige a login si es necesario
     */
    public boolean validateSessionOrRedirect(AppCompatActivity activity) {
        SessionValidationResult result = validateCurrentSession();
        
        if (!result.isValid()) {
            Log.i(TAG, "Sesión inválida, redirigiendo a login: " + result.getMessage());
            redirectToLogin(activity);
            return false;
        }
        
        return true;
    }
    
    /**
     * Inicia una nueva sesión
     */
    public void startSession(String userEmail) {
        Log.d(TAG, "Iniciando nueva sesión para: " + userEmail);
        preferencesManager.updateLastActivityTime();
        preferencesManager.setUserLoggedIn(true);
    }
    
    /**
     * Termina la sesión actual
     */
    public void endSession() {
        Log.d(TAG, "Terminando sesión");
        authRepository.logout();
        preferencesManager.setUserLoggedIn(false);
        preferencesManager.clearLastActivityTime();
    }
    
    /**
     * Redirige a la pantalla de login
     */
    private void redirectToLogin(AppCompatActivity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    
    /**
     * Actualiza la actividad del usuario (llamar en acciones importantes)
     */
    public void updateUserActivity() {
        preferencesManager.updateLastActivityTime();
    }
    
    /**
     * Verifica si el usuario necesita reautenticación
     */
    public boolean needsReauthentication() {
        long lastActivity = preferencesManager.getLastActivityTime();
        long currentTime = System.currentTimeMillis();
        long timeSinceLastActivity = currentTime - lastActivity;
        
        // Requiere reautenticación después de 1 hora de inactividad
        boolean needsReauth = timeSinceLastActivity > (60 * 60 * 1000);
        Log.d(TAG, "Necesita reautenticación: " + needsReauth);
        return needsReauth;
    }
    
    /**
     * Resultado de validación de sesión
     */
    public static class SessionValidationResult {
        private final boolean valid;
        private final String message;
        private final Reason reason;
        
        public SessionValidationResult(boolean valid, String message, Reason reason) {
            this.valid = valid;
            this.message = message;
            this.reason = reason;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public Reason getReason() { return reason; }
        
        public enum Reason {
            VALID,
            NO_USER,
            EMAIL_NOT_VERIFIED,
            SESSION_EXPIRED,
            NETWORK_ERROR
        }
    }
}
