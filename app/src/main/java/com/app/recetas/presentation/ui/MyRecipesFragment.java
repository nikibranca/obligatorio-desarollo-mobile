package com.app.recetas.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.recetas.data.local.entities.Recipe;
import com.app.recetas.presentation.ui.detail.RecipeDetailActivity;
import com.app.recetas.presentation.viewmodel.HomeViewModel;
import com.app.recetas.utils.PreferencesManager;

import java.util.List;

/**
 * Fragment para mostrar las recetas guardadas del usuario
 */
public class MyRecipesFragment extends Fragment {
    
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerViewMyRecipes;
    private TextView textEmptyState;
    private MyRecipesAdapter adapter;
    
    public static MyRecipesFragment newInstance() {
        return new MyRecipesFragment();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = createMyRecipesUI();
        
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        
        setupRecyclerView();
        observeViewModel();
        
        return rootView;
    }
    
    private View createMyRecipesUI() {
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(30, 30, 30, 30);
        
        // Título
        TextView title = new TextView(getContext());
        title.setText("📋 Mis Recetas Guardadas");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 30);
        mainLayout.addView(title);
        
        // Estado vacío
        textEmptyState = new TextView(getContext());
        textEmptyState.setText("No tienes recetas guardadas aún.\n¡Ve a la búsqueda y agrega algunas!");
        textEmptyState.setTextSize(16);
        textEmptyState.setPadding(20, 50, 20, 50);
        textEmptyState.setVisibility(View.GONE);
        mainLayout.addView(textEmptyState);
        
        // RecyclerView para las recetas
        recyclerViewMyRecipes = new RecyclerView(getContext());
        recyclerViewMyRecipes.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        mainLayout.addView(recyclerViewMyRecipes);
        
        return mainLayout;
    }
    
    private void setupRecyclerView() {
        adapter = new MyRecipesAdapter(
            // Callback para ver detalle
            recipe -> openRecipeDetail(recipe),
            // Callback para eliminar
            recipe -> {
                homeViewModel.deleteRecipe(recipe);
                Toast.makeText(getContext(), "Receta eliminada", Toast.LENGTH_SHORT).show();
            }
        );
        
        recyclerViewMyRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMyRecipes.setAdapter(adapter);
    }
    
    private void observeViewModel() {
        homeViewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            if (recipes != null) {
                adapter.setRecipes(recipes);
                
                if (recipes.isEmpty()) {
                    textEmptyState.setVisibility(View.VISIBLE);
                    recyclerViewMyRecipes.setVisibility(View.GONE);
                } else {
                    textEmptyState.setVisibility(View.GONE);
                    recyclerViewMyRecipes.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    
    private void openRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
        
        // Pasar datos de la receta guardada
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_ID, recipe.id);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_NAME, recipe.name);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_CATEGORY, recipe.category);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_AREA, recipe.area);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_INSTRUCTIONS, recipe.instructions);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_IMAGE, recipe.imageUrl);
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_INGREDIENTS, recipe.ingredients);
        
        startActivity(intent);
    }
    
    /**
     * Adapter para mostrar las recetas guardadas
     */
    private static class MyRecipesAdapter extends RecyclerView.Adapter<MyRecipesAdapter.ViewHolder> {
        
        private List<Recipe> recipes;
        private final OnRecipeClickListener onRecipeClick;
        private final OnRecipeDeleteListener onRecipeDelete;
        
        public interface OnRecipeClickListener {
            void onRecipeClick(Recipe recipe);
        }
        
        public interface OnRecipeDeleteListener {
            void onRecipeDelete(Recipe recipe);
        }
        
        public MyRecipesAdapter(OnRecipeClickListener onRecipeClick, OnRecipeDeleteListener onRecipeDelete) {
            this.onRecipeClick = onRecipeClick;
            this.onRecipeDelete = onRecipeDelete;
        }
        
        public void setRecipes(List<Recipe> recipes) {
            this.recipes = recipes;
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout itemLayout = new LinearLayout(parent.getContext());
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(20, 20, 20, 20);
            itemLayout.setBackgroundColor(0xFFF5F5F5);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 20);
            itemLayout.setLayoutParams(params);
            
            return new ViewHolder(itemLayout);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Recipe recipe = recipes.get(position);
            holder.bind(recipe, onRecipeClick, onRecipeDelete);
        }
        
        @Override
        public int getItemCount() {
            return recipes != null ? recipes.size() : 0;
        }
        
        static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textName, textCategory, textArea;
            private LinearLayout buttonLayout;
            
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                
                LinearLayout layout = (LinearLayout) itemView;
                
                textName = new TextView(itemView.getContext());
                textName.setTextSize(18);
                textName.setPadding(0, 0, 0, 10);
                layout.addView(textName);
                
                textCategory = new TextView(itemView.getContext());
                textCategory.setTextSize(14);
                layout.addView(textCategory);
                
                textArea = new TextView(itemView.getContext());
                textArea.setTextSize(14);
                textArea.setPadding(0, 0, 0, 15);
                layout.addView(textArea);
                
                buttonLayout = new LinearLayout(itemView.getContext());
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                layout.addView(buttonLayout);
            }
            
            public void bind(Recipe recipe, OnRecipeClickListener onRecipeClick, OnRecipeDeleteListener onRecipeDelete) {
                textName.setText("🍽️ " + recipe.name);
                textCategory.setText("📂 " + (recipe.category != null ? recipe.category : "Sin categoría"));
                textArea.setText("🌍 " + (recipe.area != null ? recipe.area : "Sin área"));
                
                // Limpiar botones anteriores
                buttonLayout.removeAllViews();
                
                // Botón ver detalle
                android.widget.Button btnView = new android.widget.Button(itemView.getContext());
                btnView.setText("Ver Detalle");
                btnView.setLayoutParams(new LinearLayout.LayoutParams(0, 
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                btnView.setOnClickListener(v -> onRecipeClick.onRecipeClick(recipe));
                buttonLayout.addView(btnView);
                
                // Botón eliminar
                android.widget.Button btnDelete = new android.widget.Button(itemView.getContext());
                btnDelete.setText("Eliminar");
                btnDelete.setLayoutParams(new LinearLayout.LayoutParams(0, 
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                btnDelete.setOnClickListener(v -> onRecipeDelete.onRecipeDelete(recipe));
                buttonLayout.addView(btnDelete);
            }
        }
    }
}
