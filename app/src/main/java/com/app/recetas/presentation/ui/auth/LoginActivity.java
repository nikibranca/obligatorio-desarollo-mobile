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
import com.app.recetas.utils.SessionValidator;
import com.app.recetas.utils.FirebaseConnectionValidator;

/**
 * Activity para login con Firebase Auth y validaciones completas
 */
public class LoginActivity extends AppCompatActivity {
    
    private AuthRepository authRepository;
    private SessionValidator sessionValidator;
    private EditText editEmail, editPassword;
    private Button btnLogin, btnGoToRegister;
    private TextView textStatus;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Crear UI programáticamente (simple)
        createSimpleUI();
        
        // Inicializar dependencias
        authRepository = new AuthRepository();
        sessionValidator = new SessionValidator(this);
        
        // Configurar listeners
        setupClickListeners();
        
        // Verificar conexión inicial
        checkFirebaseConnection();
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
        textStatus.setText("Verificando conexión...");
        textStatus.setPadding(0, 30, 0, 0);
        layout.addView(textStatus);
        
        setContentView(layout);
    }
    
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        btnGoToRegister.setOnClickListener(v -> goToRegister());
    }
    
    /**
     * Verifica la conexión con Firebase antes de permitir login
     */
    private void checkFirebaseConnection() {
        FirebaseConnectionValidator.validateFirebaseConnection(this, new FirebaseConnectionValidator.FirebaseConnectionCallback() {
            @Override
            public void onConnectionResult(boolean isConnected, String message) {
                if (isConnected) {
                    textStatus.setText("✅ Conexión establecida. Ingresa tus credenciales");
                    btnLogin.setEnabled(true);
                    btnGoToRegister.setEnabled(true);
                } else {
                    textStatus.setText("❌ " + message + ". Verifica tu conexión");
                    btnLogin.setEnabled(false);
                    btnGoToRegister.setEnabled(false);
                }
            }
        });
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
        btnGoToRegister.setEnabled(false);
        
        // Realizar login
        authRepository.login(email, password, task -> {
            btnLogin.setEnabled(true);
            btnGoToRegister.setEnabled(true);
            
            if (task.isSuccessful()) {
                // Login exitoso
                textStatus.setText("✅ ¡Bienvenido!");
                
                // DEBUG: Verificar estado de Firebase
                authRepository.debugFirebaseState();
                
                // Iniciar sesión en SessionValidator
                sessionValidator.startSession(email);
                
                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();
                
                // Ir a MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                
            } else {
                // Error en login
                String error = authRepository.getErrorMessage(task.getException());
                textStatus.setText("❌ " + error);
                Toast.makeText(this, "Error de login: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void goToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Verificar si ya hay una sesión válida
        if (sessionValidator.validateCurrentSession().isValid()) {
            // Ya hay sesión válida, ir a MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
