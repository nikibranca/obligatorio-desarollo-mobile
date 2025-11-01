package com.app.recetas.presentation.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.app.recetas.data.remote.dto.MealDto;
import com.app.recetas.presentation.viewmodel.SearchViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity para mostrar el detalle completo de una receta
 * Muestra nombre, categoría, área, ingredientes e instrucciones
 */
public class RecipeDetailActivity extends AppCompatActivity {
    
    // Constantes para Intent extras
    public static final String EXTRA_MEAL_ID = "meal_id";
    public static final String EXTRA_MEAL_NAME = "meal_name";
    public static final String EXTRA_MEAL_CATEGORY = "meal_category";
    public static final String EXTRA_MEAL_AREA = "meal_area";
    public static final String EXTRA_MEAL_INSTRUCTIONS = "meal_instructions";
    public static final String EXTRA_MEAL_IMAGE = "meal_image";
    public static final String EXTRA_MEAL_INGREDIENTS = "meal_ingredients";
    
    // UI Components
    private TextView textName, textCategory, textArea, textIngredients, textInstructions;
    private Button btnAddToCollection, btnBack;
    private SearchViewModel searchViewModel;
    
    // Datos de la receta
    private MealDto currentMeal;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Crear UI
        createDetailUI();
        
        // Inicializar ViewModel
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        
        // Obtener datos del Intent
        loadRecipeFromIntent();
        
        // Configurar listeners
        setupClickListeners();
        
        // Observar ViewModel
        observeViewModel();
    }
    
    /**
     * Crea la interfaz de usuario programáticamente
     */
    private void createDetailUI() {
        ScrollView scrollView = new ScrollView(this);
        
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(30, 30, 30, 30);
        
        // Header con botón atrás
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setPadding(0, 0, 0, 20);
        
        btnBack = new Button(this);
        btnBack.setText("← Atrás");
        btnBack.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT));
        headerLayout.addView(btnBack);
        
        mainLayout.addView(headerLayout);
        
        // Título de la receta
        textName = new TextView(this);
        textName.setTextSize(24);
        textName.setTextColor(0xFF000000);
        textName.setPadding(0, 0, 0, 20);
        mainLayout.addView(textName);
        
        // Categoría y área
        textCategory = new TextView(this);
        textCategory.setTextSize(16);
        textCategory.setTextColor(0xFF666666);
        textCategory.setPadding(0, 0, 0, 5);
        mainLayout.addView(textCategory);
        
        textArea = new TextView(this);
        textArea.setTextSize(16);
        textArea.setTextColor(0xFF666666);
        textArea.setPadding(0, 0, 0, 20);
        mainLayout.addView(textArea);
        
        // Ingredientes
        TextView labelIngredients = new TextView(this);
        labelIngredients.setText("🥘 INGREDIENTES");
        labelIngredients.setTextSize(18);
        labelIngredients.setTextColor(0xFF000000);
        labelIngredients.setPadding(0, 10, 0, 10);
        mainLayout.addView(labelIngredients);
        
        textIngredients = new TextView(this);
        textIngredients.setTextSize(14);
        textIngredients.setTextColor(0xFF333333);
        textIngredients.setPadding(10, 0, 0, 20);
        mainLayout.addView(textIngredients);
        
        // Instrucciones
        TextView labelInstructions = new TextView(this);
        labelInstructions.setText("📝 INSTRUCCIONES");
        labelInstructions.setTextSize(18);
        labelInstructions.setTextColor(0xFF000000);
        labelInstructions.setPadding(0, 10, 0, 10);
        mainLayout.addView(labelInstructions);
        
        textInstructions = new TextView(this);
        textInstructions.setTextSize(14);
        textInstructions.setTextColor(0xFF333333);
        textInstructions.setPadding(10, 0, 0, 30);
        mainLayout.addView(textInstructions);
        
        // Botón agregar a colección
        btnAddToCollection = new Button(this);
        btnAddToCollection.setText("➕ Agregar a mi Colección");
        btnAddToCollection.setTextSize(16);
        btnAddToCollection.setPadding(20, 20, 20, 20);
        mainLayout.addView(btnAddToCollection);
        
        scrollView.addView(mainLayout);
        setContentView(scrollView);
    }
    
    /**
     * Carga los datos de la receta desde el Intent
     */
    private void loadRecipeFromIntent() {
        Intent intent = getIntent();
        
        // Crear MealDto con los datos recibidos
        currentMeal = new MealDto();
        currentMeal.idMeal = intent.getStringExtra(EXTRA_MEAL_ID);
        currentMeal.strMeal = intent.getStringExtra(EXTRA_MEAL_NAME);
        currentMeal.strCategory = intent.getStringExtra(EXTRA_MEAL_CATEGORY);
        currentMeal.strArea = intent.getStringExtra(EXTRA_MEAL_AREA);
        currentMeal.strInstructions = intent.getStringExtra(EXTRA_MEAL_INSTRUCTIONS);
        currentMeal.strMealThumb = intent.getStringExtra(EXTRA_MEAL_IMAGE);
        
        // Obtener ingredientes (viene como JSON string)
        String ingredientsJson = intent.getStringExtra(EXTRA_MEAL_INGREDIENTS);
        parseAndSetIngredients(ingredientsJson);
        
        // Mostrar datos en la UI
        displayRecipeData();
    }
    
    /**
     * Parsea los ingredientes desde JSON y los asigna al MealDto
     */
    private void parseAndSetIngredients(String ingredientsJson) {
        if (ingredientsJson == null || ingredientsJson.isEmpty()) {
            return;
        }
        
        try {
            JSONArray ingredientsArray = new JSONArray(ingredientsJson);
            
            // Asignar ingredientes a los campos del MealDto
            for (int i = 0; i < ingredientsArray.length() && i < 20; i++) {
                JSONObject ingredientObj = ingredientsArray.getJSONObject(i);
                String ingredient = ingredientObj.optString("ingredient", "");
                String measure = ingredientObj.optString("measure", "");
                
                // Asignar a los campos correspondientes del MealDto
                switch (i) {
                    case 0: currentMeal.strIngredient1 = ingredient; currentMeal.strMeasure1 = measure; break;
                    case 1: currentMeal.strIngredient2 = ingredient; currentMeal.strMeasure2 = measure; break;
                    case 2: currentMeal.strIngredient3 = ingredient; currentMeal.strMeasure3 = measure; break;
                    case 3: currentMeal.strIngredient4 = ingredient; currentMeal.strMeasure4 = measure; break;
                    case 4: currentMeal.strIngredient5 = ingredient; currentMeal.strMeasure5 = measure; break;
                    // Agregar más casos según sea necesario
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra los datos de la receta en la UI
     */
    private void displayRecipeData() {
        // Nombre
        textName.setText(currentMeal.strMeal != null ? currentMeal.strMeal : "Sin nombre");
        
        // Categoría
        textCategory.setText("📂 Categoría: " + 
            (currentMeal.strCategory != null ? currentMeal.strCategory : "Sin categoría"));
        
        // Área
        textArea.setText("🌍 Origen: " + 
            (currentMeal.strArea != null ? currentMeal.strArea : "Sin área"));
        
        // Ingredientes
        String ingredientsList = buildIngredientsList();
        textIngredients.setText(ingredientsList.isEmpty() ? "No hay ingredientes disponibles" : ingredientsList);
        
        // Instrucciones
        textInstructions.setText(currentMeal.strInstructions != null ? 
            currentMeal.strInstructions : "No hay instrucciones disponibles");
    }
    
    /**
     * Construye la lista de ingredientes formateada
     */
    private String buildIngredientsList() {
        StringBuilder ingredients = new StringBuilder();
        
        String[] ingredientFields = {
            currentMeal.strIngredient1, currentMeal.strIngredient2, currentMeal.strIngredient3,
            currentMeal.strIngredient4, currentMeal.strIngredient5, currentMeal.strIngredient6,
            currentMeal.strIngredient7, currentMeal.strIngredient8, currentMeal.strIngredient9,
            currentMeal.strIngredient10, currentMeal.strIngredient11, currentMeal.strIngredient12,
            currentMeal.strIngredient13, currentMeal.strIngredient14, currentMeal.strIngredient15,
            currentMeal.strIngredient16, currentMeal.strIngredient17, currentMeal.strIngredient18,
            currentMeal.strIngredient19, currentMeal.strIngredient20
        };
        
        String[] measureFields = {
            currentMeal.strMeasure1, currentMeal.strMeasure2, currentMeal.strMeasure3,
            currentMeal.strMeasure4, currentMeal.strMeasure5, currentMeal.strMeasure6,
            currentMeal.strMeasure7, currentMeal.strMeasure8, currentMeal.strMeasure9,
            currentMeal.strMeasure10, currentMeal.strMeasure11, currentMeal.strMeasure12,
            currentMeal.strMeasure13, currentMeal.strMeasure14, currentMeal.strMeasure15,
            currentMeal.strMeasure16, currentMeal.strMeasure17, currentMeal.strMeasure18,
            currentMeal.strMeasure19, currentMeal.strMeasure20
        };
        
        for (int i = 0; i < ingredientFields.length; i++) {
            String ingredient = ingredientFields[i];
            String measure = measureFields[i];
            
            if (ingredient != null && !ingredient.trim().isEmpty()) {
                ingredients.append("• ");
                
                if (measure != null && !measure.trim().isEmpty()) {
                    ingredients.append(measure.trim()).append(" ");
                }
                
                ingredients.append(ingredient.trim()).append("\n");
            }
        }
        
        return ingredients.toString();
    }
    
    /**
     * Configura los listeners de los botones
     */
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnAddToCollection.setOnClickListener(v -> {
            if (currentMeal != null) {
                searchViewModel.addToCollection(currentMeal);
            }
        });
    }
    
    /**
     * Observa los cambios del ViewModel
     */
    private void observeViewModel() {
        searchViewModel.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                
                // Si se agregó exitosamente, cambiar botón
                if (message.contains("agregada")) {
                    btnAddToCollection.setText("✅ Agregada a tu Colección");
                    btnAddToCollection.setEnabled(false);
                }
            }
        });
        
        searchViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
