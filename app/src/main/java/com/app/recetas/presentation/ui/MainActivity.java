package com.app.recetas.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // <-- IMPORTANTE para View.generateViewId()
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.recetas.data.repository.AuthRepository;
import com.app.recetas.presentation.ui.auth.LoginActivity;
import com.app.recetas.presentation.ui.search.SearchFragment;
import com.app.recetas.presentation.ui.collection.MyRecipesFragment; // <-- si creaste el fragment nuevo
import com.app.recetas.presentation.viewmodel.HomeViewModel;
import com.app.recetas.utils.PreferencesManager;



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

    // ID del contenedor para los fragments (evitamos depender de R.id.*)
    private int containerId;

   /* @Override
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
        showLastRecipeBannerIfAny();
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createMainUI();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        authRepository = new AuthRepository();
        setupClickListeners();
        observeData();

        // 👇 UNIFICADO
        refreshHeader();

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
        btnSearch.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        navLayout.addView(btnSearch);

        btnMyRecipes = new Button(this);
        btnMyRecipes.setText("📋 Mis Recetas");
        btnMyRecipes.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        navLayout.addView(btnMyRecipes);

        btnLogout = new Button(this);
        btnLogout.setText("🚪 Salir");
        btnLogout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        navLayout.addView(btnLogout);

        mainLayout.addView(navLayout);

        // Container para fragments (con id generado, NO android.R.id.content)
        LinearLayout fragmentContainer = new LinearLayout(this);
        fragmentContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        containerId = View.generateViewId();     // <-- generamos id único
        fragmentContainer.setId(containerId);    // <-- lo asignamos al contenedor

        mainLayout.addView(fragmentContainer);

        setContentView(mainLayout);
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> performLogout());
        btnSearch.setOnClickListener(v -> showSearchFragment());
        btnMyRecipes.setOnClickListener(v -> showMyRecipesInfo());
    }

   /* private void observeData() {
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
*/
   private void observeData() {
       homeViewModel.getRecipes().observe(this, recipes -> {
           // Antes: updateInfoDisplay(...)
           // Ahora: SIEMPRE recalculamos el header completo
           refreshHeader();
       });

       homeViewModel.getLastRecipeInfo().observe(this, lastRecipeInfo -> {
           // Este observable adicional ya no es necesario para el banner,
           // pero si querés mantenerlo, podés simplemente volver a refrescar:
           refreshHeader();
       });
   }

    private void showUserInfo() {
        String userEmail = authRepository.getCurrentUserEmail();
        String userInfo = "👤 " + (userEmail != null ? userEmail : "Usuario de prueba");
        textInfo.setText(userInfo);
    }

    private void showLastRecipeBannerIfAny() {
        PreferencesManager pm = new PreferencesManager(this);
        String[] last = pm.getLastRecipe(); // [id, name, timestamp] o null
        if (last != null && last.length == 3) {
            String lastName = last[1];
            long ts = 0L;
            try { ts = Long.parseLong(last[2]); } catch (Exception ignored) {}
            String when = (ts > 0)
                    ? android.text.format.DateFormat.format("dd/MM HH:mm", new java.util.Date(ts)).toString()
                    : "";

            String base = textInfo.getText().toString();
            String banner = "\n📝 Última receta agregada/modificada: " + lastName +
                    (when.isEmpty() ? "" : " (" + when + ")");

            if (!base.contains("Última receta agregada/modificada")) {
                textInfo.setText(base + banner);
            }
        }
    }


    private void updateInfoDisplay(int recipeCount) {
        String userEmail = authRepository.getCurrentUserEmail();

        String info = "👤 " + (userEmail != null ? userEmail : "Usuario de prueba") + "\n" +
                "📊 Recetas guardadas: " + recipeCount;

        textInfo.setText(info);
    }

    // Construye SIEMPRE el encabezado completo (usuario, cantidad y última receta)
    private void refreshHeader() {
        // 1) Usuario
        String userEmail = authRepository.getCurrentUserEmail();
        String header = "👤 " + (userEmail != null ? userEmail : "Usuario de prueba");

        // 2) Cantidad de recetas guardadas (viene del LiveData del ViewModel)
        int count = 0;
        if (homeViewModel.getRecipes().getValue() != null) {
            count = homeViewModel.getRecipes().getValue().size();
        }
        header += "\n📊 Recetas guardadas: " + count;

        // 3) Última receta (PreferencesManager de TU proyecto)
        PreferencesManager pm = new PreferencesManager(this);
        String[] last = pm.getLastRecipe(); // [id, name, timestamp] o null
        if (last != null && last.length == 3) {
            String lastName = last[1];
            long ts = 0L;
            try { ts = Long.parseLong(last[2]); } catch (Exception ignored) {}
            String when = (ts > 0)
                    ? android.text.format.DateFormat.format("dd/MM HH:mm", new java.util.Date(ts)).toString()
                    : "";
            header += "\n📝 Última receta agregada/modificada: " + lastName +
                    (when.isEmpty() ? "" : " (" + when + ")");
        }

        textInfo.setText(header);
    }


    /**
     * Muestra el fragment de búsqueda
     */
    private void showSearchFragment() {
        SearchFragment searchFragment = new SearchFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, searchFragment) // <-- usamos containerId
                .commit();

        currentFragment = searchFragment;

        // Actualizar botones
        btnSearch.setEnabled(false);
        btnMyRecipes.setEnabled(true);
    }

    /**
     * Muestra información de recetas guardadas (y el fragment real)
     */
    private void showMyRecipesInfo() {
        // (Podés dejar el layout informativo si querés, pero lo ideal es mostrar tu fragment)
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(containerId, MyRecipesFragment.newInstance(), "MY_RECIPES") // <-- containerId
                .addToBackStack("MY_RECIPES")
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
