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
 * Activity básica para registro con Firebase Auth
 */
public class RegisterActivity extends AppCompatActivity {
    
    private AuthRepository authRepository;
    private EditText editEmail, editPassword, editConfirmPassword;
    private Button btnRegister, btnGoToLogin;
    private TextView textStatus;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Crear UI programáticamente
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
        title.setText("🍽️ Crear Cuenta");
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
        
        // Confirm Password
        TextView labelConfirm = new TextView(this);
        labelConfirm.setText("Confirmar Contraseña:");
        labelConfirm.setPadding(0, 20, 0, 0);
        layout.addView(labelConfirm);
        
        editConfirmPassword = new EditText(this);
        editConfirmPassword.setHint("Repetir contraseña");
        editConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editConfirmPassword.setPadding(20, 20, 20, 20);
        layout.addView(editConfirmPassword);
        
        // Botón Register
        btnRegister = new Button(this);
        btnRegister.setText("Crear Cuenta");
        btnRegister.setPadding(0, 30, 0, 0);
        layout.addView(btnRegister);
        
        // Botón Login
        btnGoToLogin = new Button(this);
        btnGoToLogin.setText("¿Ya tienes cuenta? Inicia sesión");
        layout.addView(btnGoToLogin);
        
        // Status
        textStatus = new TextView(this);
        textStatus.setText("Completa los datos para crear tu cuenta");
        textStatus.setPadding(0, 30, 0, 0);
        layout.addView(textStatus);
        
        setContentView(layout);
    }
    
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        btnGoToLogin.setOnClickListener(v -> goToLogin());
    }
    
    private void performRegister() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();
        
        // Validar email
        InputValidator.ValidationResult emailValidation = InputValidator.validateEmail(email);
        if (!emailValidation.isValid) {
            textStatus.setText("❌ " + emailValidation.errorMessage);
            return;
        }
        
        // Validar password
        InputValidator.ValidationResult passwordValidation = InputValidator.validatePassword(password);
        if (!passwordValidation.isValid) {
            textStatus.setText("❌ " + passwordValidation.errorMessage);
            return;
        }
        
        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            textStatus.setText("❌ Las contraseñas no coinciden");
            return;
        }
        
        // Mostrar loading
        textStatus.setText("🔄 Creando cuenta...");
        btnRegister.setEnabled(false);
        
        // Realizar registro
        authRepository.register(email, password, task -> {
            btnRegister.setEnabled(true);
            
            if (task.isSuccessful()) {
                // Registro exitoso
                textStatus.setText("✅ ¡Cuenta creada exitosamente!");
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                
                // Ir a MainActivity
                startActivity(new Intent(this, MainActivity.class));
                finish();
                
            } else {
                // Error en registro
                String error = task.getException() != null ? 
                    task.getException().getMessage() : "Error desconocido";
                textStatus.setText("❌ Error: " + error);
                Toast.makeText(this, "Error de registro", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
