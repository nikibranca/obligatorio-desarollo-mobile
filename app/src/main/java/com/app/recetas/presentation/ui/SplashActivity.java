package com.app.recetas.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.app.recetas.data.repository.AuthRepository;
import com.app.recetas.presentation.ui.auth.LoginActivity;

/**
 * SplashActivity que verifica autenticación y redirige apropiadamente
 */
public class SplashActivity extends AppCompatActivity {
    
    private AuthRepository authRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Crear splash screen simple
        TextView splashText = new TextView(this);
        splashText.setText("🍽️\n\nMis Recetas\n\nCargando...");
        splashText.setTextSize(24);
        splashText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        splashText.setPadding(50, 200, 50, 50);
        setContentView(splashText);
        
        // Inicializar repositorio de autenticación
        authRepository = new AuthRepository();
        
        // Verificar autenticación después de 2 segundos
        new Handler().postDelayed(() -> {
            checkAuthenticationAndNavigate();
        }, 2000);
    }
    
    /**
     * Verifica si el usuario está autenticado y navega apropiadamente
     */
    private void checkAuthenticationAndNavigate() {
        if (authRepository.isUserLoggedIn()) {
            // Usuario ya está logueado, ir a MainActivity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Usuario no está logueado, ir a LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish(); // Cerrar SplashActivity
    }
}
