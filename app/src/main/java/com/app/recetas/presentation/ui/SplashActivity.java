package com.app.recetas.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.app.recetas.data.repository.AuthRepository;
import com.app.recetas.presentation.ui.auth.LoginActivity;
import com.app.recetas.utils.FirebaseConnectionValidator;
import com.app.recetas.utils.SessionValidator;

/**
 * SplashActivity que verifica conexión, autenticación y sesión
 */
public class SplashActivity extends AppCompatActivity {
    
    private AuthRepository authRepository;
    private SessionValidator sessionValidator;
    private TextView splashText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Crear splash screen simple
        splashText = new TextView(this);
        splashText.setText("🍽️\n\nMis Recetas\n\nCargando...");
        splashText.setTextSize(24);
        splashText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        splashText.setPadding(50, 200, 50, 50);
        setContentView(splashText);
        
        // Inicializar dependencias
        authRepository = new AuthRepository();
        sessionValidator = new SessionValidator(this);
        
        // Iniciar validaciones
        startValidationProcess();
    }
    
    /**
     * Inicia el proceso de validación completo
     */
    private void startValidationProcess() {
        updateSplashText("Verificando conexión...");
        
        // 1. Verificar conexión con Firebase
        FirebaseConnectionValidator.validateFirebaseConnection(this, new FirebaseConnectionValidator.FirebaseConnectionCallback() {
            @Override
            public void onConnectionResult(boolean isConnected, String message) {
                if (isConnected) {
                    // Conexión exitosa, verificar sesión
                    updateSplashText("Verificando sesión...");
                    new Handler().postDelayed(() -> validateSessionAndNavigate(), 1000);
                } else {
                    // Sin conexión, mostrar error y ir a login
                    updateSplashText("Sin conexión a Firebase");
                    Toast.makeText(SplashActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(() -> navigateToLogin(), 2000);
                }
            }
        });
    }
    
    /**
     * Valida la sesión y navega apropiadamente
     */
    private void validateSessionAndNavigate() {
        SessionValidator.SessionValidationResult result = sessionValidator.validateCurrentSession();
        
        if (result.isValid()) {
            // Sesión válida, ir a MainActivity
            updateSplashText("Sesión válida, ingresando...");
            new Handler().postDelayed(() -> navigateToMain(), 1000);
        } else {
            // Sesión inválida, ir a LoginActivity
            updateSplashText("Sesión expirada");
            new Handler().postDelayed(() -> navigateToLogin(), 1000);
        }
    }
    
    /**
     * Navega a MainActivity
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    /**
     * Navega a LoginActivity
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    /**
     * Actualiza el texto del splash screen
     */
    private void updateSplashText(String message) {
        if (splashText != null) {
            splashText.setText("🍽️\n\nMis Recetas\n\n" + message);
        }
    }
}
