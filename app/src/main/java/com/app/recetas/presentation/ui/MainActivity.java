package com.app.recetas.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.recetas.data.repository.AuthRepository;
import com.app.recetas.presentation.ui.auth.LoginActivity;
import com.app.recetas.presentation.ui.search.SearchFragment;
import com.app.recetas.presentation.viewmodel.HomeViewModel;

/**
 * MainActivity principal con navegación entre fragments
 */
public class MainActivity extends AppCompatActivity {
    
    private HomeViewModel homeViewModel;
    private AuthRepository authRepository;
    private TextView textInfo;
    private Button btnLogout, btnSearch, btnMyRecipes;
    
    // Fragment actual
    private Fragment currentFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Crear layout con navegación
        createMainUI();
        
        // Inicializar dependencias
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        authRepository = new AuthRepository();
        
        // Configurar listeners
        setupClickListeners();
        
        // Observar datos
        observeData();
        
        // Mostrar información del usuario
        showUserInfo();
        
        // Mostrar fragment de búsqueda por defecto
        showSearchFragment();
    }
    
    private void createMainUI() {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        
        // Header con info del usuario
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        headerLayout.setPadding(30, 30, 30, 20);
        headerLayout.setBackgroundColor(0xFFE3F2FD);
        
        TextView title = new TextView(this);
        title.setText("🍽️ Mis Recetas");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 10);
        headerLayout.addView(title);
        
        textInfo = new TextView(this);
        textInfo.setText("Cargando información...");
        textInfo.setTextSize(14);
        headerLayout.addView(textInfo);
        
        mainLayout.addView(headerLayout);
        
        // Navegación
        LinearLayout navLayout = new LinearLayout(this);
        navLayout.setOrientation(LinearLayout.HORIZONTAL);
        navLayout.setPadding(20, 10, 20, 10);
        navLayout.setBackgroundColor(0xFFF5F5F5);
        
        btnSearch = new Button(this);
        btnSearch.setText("🔍 Buscar");
        btnSearch.setLayoutParams(new LinearLayout.LayoutParams(0, 
            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        navLayout.addView(btnSearch);
        
        btnMyRecipes = new Button(this);
        btnMyRecipes.setText("📋 Mis Recetas");
        btnMyRecipes.setLayoutParams(new LinearLayout.LayoutParams(0, 
            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        navLayout.addView(btnMyRecipes);
        
        btnLogout = new Button(this);
        btnLogout.setText("🚪 Salir");
        btnLogout.setLayoutParams(new LinearLayout.LayoutParams(0, 
            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        navLayout.addView(btnLogout);
        
        mainLayout.addView(navLayout);
        
        // Container para fragments
        LinearLayout fragmentContainer = new LinearLayout(this);
        fragmentContainer.setId(android.R.id.content);
        fragmentContainer.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        mainLayout.addView(fragmentContainer);
        
        setContentView(mainLayout);
    }
    
    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> performLogout());
        btnSearch.setOnClickListener(v -> showSearchFragment());
        btnMyRecipes.setOnClickListener(v -> showMyRecipesInfo());
    }
    
    private void observeData() {
        // Observar recetas para mostrar estadísticas
        homeViewModel.getRecipes().observe(this, recipes -> {
            updateInfoDisplay(recipes != null ? recipes.size() : 0);
        });
        
        // Observar información de última receta
        homeViewModel.getLastRecipeInfo().observe(this, lastRecipeInfo -> {
            if (lastRecipeInfo != null && !lastRecipeInfo.isEmpty()) {
                String currentText = textInfo.getText().toString();
                if (!currentText.contains("📝")) {
                    textInfo.setText(currentText + "\n📝 " + lastRecipeInfo);
                }
            }
        });
    }
    
    private void showUserInfo() {
        String userEmail = authRepository.getCurrentUserEmail();
        String userInfo = "👤 " + (userEmail != null ? userEmail : "Usuario de prueba");
        textInfo.setText(userInfo);
    }
    
    private void updateInfoDisplay(int recipeCount) {
        String userEmail = authRepository.getCurrentUserEmail();
        
        String info = "👤 " + (userEmail != null ? userEmail : "Usuario de prueba") + "\n" +
                     "📊 Recetas guardadas: " + recipeCount;
        
        textInfo.setText(info);
    }
    
    /**
     * Muestra el fragment de búsqueda
     */
    private void showSearchFragment() {
        SearchFragment searchFragment = new SearchFragment();
        
        getSupportFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, searchFragment)
            .commit();
            
        currentFragment = searchFragment;
        
        // Actualizar botones
        btnSearch.setEnabled(false);
        btnMyRecipes.setEnabled(true);
    }
    
    /**
     * Muestra información de recetas guardadas (placeholder)
     */
    private void showMyRecipesInfo() {
        // Por ahora solo mostrar info, después implementaremos HomeFragment
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setPadding(50, 100, 50, 50);
        
        TextView title = new TextView(this);
        title.setText("📋 Mis Recetas Guardadas");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 30);
        infoLayout.addView(title);
        
        TextView info = new TextView(this);
        info.setText("Aquí aparecerán tus recetas guardadas.\n\n" +
                    "Por ahora, usa la búsqueda para encontrar y agregar recetas a tu colección.\n\n" +
                    "🔍 Ve a 'Buscar' para explorar recetas de TheMealDB");
        info.setTextSize(16);
        infoLayout.addView(info);
        
        getSupportFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, new Fragment() {
                @Override
                public android.view.View onCreateView(android.view.LayoutInflater inflater, 
                    android.view.ViewGroup container, Bundle savedInstanceState) {
                    return infoLayout;
                }
            })
            .commit();
            
        // Actualizar botones
        btnSearch.setEnabled(true);
        btnMyRecipes.setEnabled(false);
    }
    
    private void performLogout() {
        // Cerrar sesión
        authRepository.logout();
        
        // Ir a LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
