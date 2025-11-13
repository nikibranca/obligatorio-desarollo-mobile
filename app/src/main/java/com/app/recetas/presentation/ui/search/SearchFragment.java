package com.app.recetas.presentation.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.app.recetas.data.remote.dto.MealDto;
import com.app.recetas.presentation.ui.detail.RecipeDetailActivity;
import com.app.recetas.presentation.ui.search.adapter.SearchResultAdapter;
import com.app.recetas.presentation.viewmodel.SearchViewModel;
import com.app.recetas.utils.SearchType;
import com.app.recetas.utils.PreferencesManager;



/**
 * Fragment para búsqueda de recetas en TheMealDB API
 * Permite buscar por nombre, categoría o área geográfica
 */
public class SearchFragment extends Fragment {
    
    // ViewModels
    private SearchViewModel searchViewModel;
    
    // UI Components
    private EditText editSearchTerm;
    private Spinner spinnerSearchType, spinnerCategory, spinnerArea;
    private Button btnSearch, btnRandomRecipe;
    private TextView textStatus;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewResults;
    
    // Adapter
    private SearchResultAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Crear UI programáticamente
        View rootView = createSearchUI();
        
        // Inicializar ViewModel
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        
        // Configurar RecyclerView
        setupRecyclerView();
        
        // Configurar listeners
        setupClickListeners();
        
        // Observar datos del ViewModel
        observeViewModel();
        
        return rootView;
    }
    
    /**
     * Crea la interfaz de usuario programáticamente
     */
    private View createSearchUI() {
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(30, 30, 30, 30);
        
        // Título
        TextView title = new TextView(getContext());
        title.setText("🔍 Buscar Recetas");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 30);
        mainLayout.addView(title);
        
        // Tipo de búsqueda
        TextView labelSearchType = new TextView(getContext());
        labelSearchType.setText("Tipo de búsqueda:");
        labelSearchType.setPadding(0, 10, 0, 5);
        mainLayout.addView(labelSearchType);
        
        spinnerSearchType = new Spinner(getContext());
        String[] searchTypes = {"Por nombre", "Por categoría", "Por área"};
        ArrayAdapter<String> searchTypeAdapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, searchTypes);
        searchTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearchType.setAdapter(searchTypeAdapter);
        mainLayout.addView(spinnerSearchType);
        
        // Campo de búsqueda por nombre
        TextView labelSearch = new TextView(getContext());
        labelSearch.setText("Buscar receta:");
        labelSearch.setPadding(0, 20, 0, 5);
        mainLayout.addView(labelSearch);
        
        editSearchTerm = new EditText(getContext());
        editSearchTerm.setHint("Ej: pasta, chicken, pizza...");
        editSearchTerm.setPadding(20, 20, 20, 20);
        mainLayout.addView(editSearchTerm);
        
        // Spinner de categorías
        TextView labelCategory = new TextView(getContext());
        labelCategory.setText("O selecciona categoría:");
        labelCategory.setPadding(0, 20, 0, 5);
        mainLayout.addView(labelCategory);
        
        spinnerCategory = new Spinner(getContext());
        mainLayout.addView(spinnerCategory);
        
        // Spinner de áreas
        TextView labelArea = new TextView(getContext());
        labelArea.setText("O selecciona área:");
        labelArea.setPadding(0, 20, 0, 5);
        mainLayout.addView(labelArea);
        
        spinnerArea = new Spinner(getContext());
        mainLayout.addView(spinnerArea);
        
        // Botones
        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 30, 0, 20);
        
        btnSearch = new Button(getContext());
        btnSearch.setText("Buscar");
        btnSearch.setLayoutParams(new LinearLayout.LayoutParams(0, 
            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        buttonLayout.addView(btnSearch);
        
        btnRandomRecipe = new Button(getContext());
        btnRandomRecipe.setText("Receta Aleatoria");
        btnRandomRecipe.setLayoutParams(new LinearLayout.LayoutParams(0, 
            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        buttonLayout.addView(btnRandomRecipe);
        
        mainLayout.addView(buttonLayout);
        
        // Status y loading
        textStatus = new TextView(getContext());
        textStatus.setText("Ingresa un término de búsqueda o prueba una receta aleatoria");
        textStatus.setPadding(0, 0, 0, 10);
        mainLayout.addView(textStatus);
        
        progressBar = new ProgressBar(getContext());
        progressBar.setVisibility(View.GONE);
        mainLayout.addView(progressBar);
        
        // RecyclerView para resultados
        recyclerViewResults = new RecyclerView(getContext());
        recyclerViewResults.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        mainLayout.addView(recyclerViewResults);
        
        return mainLayout;
    }
    
    /**
     * Configura el RecyclerView con su adapter
     */
    /**
     * Configura el RecyclerView con su adapter
     */
   /* private void setupRecyclerView() {
        adapter = new SearchResultAdapter(
                // Callback para "Agregar a colección"
                meal -> {
                    // 1) Insertar en Room (como ya lo hacías)
                    searchViewModel.addToCollection(meal);

                    // 2) Guardar como "última receta ingresada/modificada" (Punto F)
                    //    Ajustá los nombres si tu DTO difiere (en tu caso son públicos idMeal / strMeal).
                    new PreferencesManager(requireContext())
                            .saveLastRecipe(meal.idMeal, meal.strMeal);
                },

                // Callback para ver detalle de receta
                meal -> openRecipeDetail(meal)
        );

        recyclerViewResults.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewResults.setAdapter(adapter);
    }
*/
    /**
     * Configura el RecyclerView con su adapter
     */
    private void setupRecyclerView() {
        adapter = new SearchResultAdapter(
                // Callback para "Agregar a colección"
                meal -> {
                    // 1) Insertar en Room
                    searchViewModel.addToCollection(meal);

                    // 2) Guardar como "última receta" (TU PreferencesManager)
                    new PreferencesManager(requireContext())
                            .saveLastRecipe(meal.idMeal, meal.strMeal);
                },
                // Callback para ver detalle
                meal -> openRecipeDetail(meal)
        );

        recyclerViewResults.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewResults.setAdapter(adapter);
    }




    /**
     * Abre la pantalla de detalle de receta
     */
    private void openRecipeDetail(MealDto meal) {
        Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
        
        // Pasar datos de la receta
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_ID, meal.idMeal);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_NAME, meal.strMeal);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_CATEGORY, meal.strCategory);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_AREA, meal.strArea);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_INSTRUCTIONS, meal.strInstructions);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_IMAGE, meal.strMealThumb);
        
        // Convertir ingredientes a JSON para pasar
        String ingredientsJson = meal.buildIngredientsJson();
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_INGREDIENTS, ingredientsJson);
        
        startActivity(intent);
    }
    
    /**
     * Configura los listeners de los botones
     */
    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> performSearch());
        btnRandomRecipe.setOnClickListener(v -> searchViewModel.getRandomRecipe());
        
        // Listener para cambio de tipo de búsqueda
        spinnerSearchType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateUIForSearchType(position);
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }
    
    /**
     * Actualiza la UI según el tipo de búsqueda seleccionado
     */
    private void updateUIForSearchType(int position) {
        switch (position) {
            case 0: // Por nombre
                editSearchTerm.setVisibility(View.VISIBLE);
                spinnerCategory.setVisibility(View.GONE);
                spinnerArea.setVisibility(View.GONE);
                break;
            case 1: // Por categoría
                editSearchTerm.setVisibility(View.GONE);
                spinnerCategory.setVisibility(View.VISIBLE);
                spinnerArea.setVisibility(View.GONE);
                break;
            case 2: // Por área
                editSearchTerm.setVisibility(View.GONE);
                spinnerCategory.setVisibility(View.GONE);
                spinnerArea.setVisibility(View.VISIBLE);
                break;
        }
    }
    
    /**
     * Realiza la búsqueda según el tipo seleccionado
     */
    private void performSearch() {
        int searchTypePosition = spinnerSearchType.getSelectedItemPosition();
        String query = "";
        SearchType searchType = SearchType.NAME;
        
        switch (searchTypePosition) {
            case 0: // Por nombre
                query = editSearchTerm.getText().toString().trim();
                searchType = SearchType.NAME;
                break;
            case 1: // Por categoría
                if (spinnerCategory.getSelectedItem() != null) {
                    query = spinnerCategory.getSelectedItem().toString();
                    if (!query.equals("Seleccionar categoría")) {
                        searchType = SearchType.CATEGORY;
                    } else {
                        query = "";
                    }
                }
                break;
            case 2: // Por área
                if (spinnerArea.getSelectedItem() != null) {
                    query = spinnerArea.getSelectedItem().toString();
                    if (!query.equals("Seleccionar área")) {
                        searchType = SearchType.AREA;
                    } else {
                        query = "";
                    }
                }
                break;
        }
        
        if (query.isEmpty()) {
            textStatus.setText("❌ Ingresa un término de búsqueda válido");
            return;
        }
        
        // Realizar búsqueda
        searchViewModel.searchRecipes(query, searchType);
    }
    
    /**
     * Observa los datos del ViewModel
     */
    private void observeViewModel() {
        // Observar resultados de búsqueda
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), results -> {
            if (results != null) {
                adapter.setMeals(results);
                if (results.isEmpty()) {
                    textStatus.setText("No se encontraron recetas. Prueba con otro término.");
                } else {
                    textStatus.setText("✅ " + results.size() + " recetas encontradas. Haz clic para ver detalles.");
                }
            }
        });
        
        // Observar categorías para spinner
        searchViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);
            }
        });
        
        // Observar áreas para spinner
        searchViewModel.getAreas().observe(getViewLifecycleOwner(), areas -> {
            if (areas != null && !areas.isEmpty()) {
                ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, areas);
                areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerArea.setAdapter(areaAdapter);
            }
        });
        
        // Observar estado de loading
        searchViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                btnSearch.setEnabled(!isLoading);
                btnRandomRecipe.setEnabled(!isLoading);
                
                if (isLoading) {
                    textStatus.setText("🔄 Buscando recetas...");
                }
            }
        });
        
        // Observar mensajes
        searchViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                textStatus.setText("✅ " + message);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observar errores
        searchViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                textStatus.setText("❌ " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
