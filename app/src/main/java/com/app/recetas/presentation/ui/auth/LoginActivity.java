package com.app.recetas.presentation.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.app.recetas.data.repository.AuthRepository;
import com.app.recetas.presentation.ui.MainActivity;
import com.app.recetas.utils.InputValidator;

/**
 * Activity básica para login con Firebase Auth
 */
public class LoginActivity extends AppCompatActivity {
    
    private AuthRepository authRepository;
    private EditText editEmail, editPassword;
    private Button btnLogin, btnGoToRegister;
    private TextView textStatus;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Crear UI programáticamente (simple)
        createSimpleUI();
        
        // Inicializar repositorio
        authRepository = new AuthRepository();
        
        // Configurar listeners
        setupClickListeners();
    }
    
    private void createSimpleUI() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 100, 50, 50);
        
        // Título
        TextView title = new TextView(this);
        title.setText("🍽️ Mis Recetas - Login");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 50);
        layout.addView(title);
        
        // Email
        TextView labelEmail = new TextView(this);
        labelEmail.setText("Email:");
        layout.addView(labelEmail);
        
        editEmail = new EditText(this);
        editEmail.setHint("correo@ejemplo.com");
        editEmail.setPadding(20, 20, 20, 20);
        layout.addView(editEmail);
        
        // Password
        TextView labelPassword = new TextView(this);
        labelPassword.setText("Contraseña:");
        labelPassword.setPadding(0, 20, 0, 0);
        layout.addView(labelPassword);
        
        editPassword = new EditText(this);
        editPassword.setHint("Mínimo 6 caracteres");
        editPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editPassword.setPadding(20, 20, 20, 20);
        layout.addView(editPassword);
        
        // Botón Login
        btnLogin = new Button(this);
        btnLogin.setText("Iniciar Sesión");
        btnLogin.setPadding(0, 30, 0, 0);
        layout.addView(btnLogin);
        
        // Botón Registro
        btnGoToRegister = new Button(this);
        btnGoToRegister.setText("¿No tienes cuenta? Regístrate");
        layout.addView(btnGoToRegister);
        
        // Status
        textStatus = new TextView(this);
        textStatus.setText("Ingresa tus credenciales");
        textStatus.setPadding(0, 30, 0, 0);
        layout.addView(textStatus);
        
        setContentView(layout);
    }
    
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        btnGoToRegister.setOnClickListener(v -> goToRegister());
    }
    
    private void performLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        
        // Validar entrada
        InputValidator.ValidationResult emailValidation = InputValidator.validateEmail(email);
        if (!emailValidation.isValid) {
            textStatus.setText("❌ " + emailValidation.errorMessage);
            return;
        }
        
        InputValidator.ValidationResult passwordValidation = InputValidator.validatePassword(password);
        if (!passwordValidation.isValid) {
            textStatus.setText("❌ " + passwordValidation.errorMessage);
            return;
        }
        
        // Mostrar loading
        textStatus.setText("🔄 Iniciando sesión...");
        btnLogin.setEnabled(false);
        
        // Realizar login
        authRepository.login(email, password, task -> {
            btnLogin.setEnabled(true);
            
            if (task.isSuccessful()) {
                // Login exitoso
                textStatus.setText("✅ ¡Bienvenido!");
                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();
                
                // Ir a MainActivity
                startActivity(new Intent(this, MainActivity.class));
                finish();
                
            } else {
                // Error en login
                String error = task.getException() != null ? 
                    task.getException().getMessage() : "Error desconocido";
                textStatus.setText("❌ Error: " + error);
                Toast.makeText(this, "Error de login", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void goToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
