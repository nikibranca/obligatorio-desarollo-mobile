package com.app.recetas.presentation.ui.collection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.recetas.data.local.entities.Recipe;
import com.app.recetas.presentation.viewmodel.HomeViewModel;
import com.app.recetas.presentation.ui.detail.RecipeDetailActivity;
import com.app.recetas.utils.PreferencesManager;

public class MyRecipesFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private SavedRecipesAdapter adapter;
    private TextView emptyView;

    public MyRecipesFragment() { }

    public static MyRecipesFragment newInstance() {
        return new MyRecipesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = (int)(16 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(requireContext());
        title.setText("📋 Mis Recetas Guardadas");
        title.setTextSize(22f);
        root.addView(title);

        emptyView = new TextView(requireContext());
        emptyView.setText("No tenés recetas todavía.\nUsá 'Buscar' para agregarlas.");
        emptyView.setTextSize(16f);
        emptyView.setPadding(0, pad, 0, pad);
        root.addView(emptyView);

        RecyclerView rv = new RecyclerView(requireContext());
        rv.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Adapter con las 3 acciones: Ver, Notas, Eliminar
        adapter = new SavedRecipesAdapter(new SavedRecipesAdapter.OnRecipeAction() {
            @Override
            public void onOpen(Recipe r) {
                openSavedRecipeDetail(r);
            }

            @Override
            public void onEditNotes(Recipe r) {
                showNotesDialog(r);
            }

            @Override
            public void onDelete(Recipe r) {
                homeViewModel.deleteRecipe(r);
            }
        });

        rv.setAdapter(adapter);
        root.addView(rv);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        homeViewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            adapter.submit(recipes);
            boolean isEmpty = (recipes == null || recipes.isEmpty());
            emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        });
    }

    // ---------- Editar notas ----------

    private void showNotesDialog(final Recipe r) {
        final EditText input = new EditText(requireContext());
        input.setText(r.getPersonalNotes() != null ? r.getPersonalNotes() : "");
        new AlertDialog.Builder(requireContext())
                .setTitle("Editar notas")
                .setView(input)
                .setPositiveButton("Guardar", (d, which) -> {
                    String newNotes = input.getText().toString().trim();
                    // Actualizar en la base de datos vía ViewModel
                    homeViewModel.updateRecipeNotes(r, newNotes);

                    // Registrar como última modificada
                    new PreferencesManager(requireContext())
                            .saveLastRecipe(r.getId(), r.getName());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ---------- Ver detalle de la receta guardada ----------

    private void openSavedRecipeDetail(Recipe r) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);

        // Reutilizamos los mismos extras que usa SearchFragment para MealDto
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_ID, r.getId());
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_NAME, r.getName());
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_CATEGORY, r.getCategory());
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_AREA, r.getArea());
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_INSTRUCTIONS, r.getInstructions());
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_IMAGE, r.getImageUrl());
        intent.putExtra(RecipeDetailActivity.EXTRA_MEAL_INGREDIENTS, r.getIngredients());

        startActivity(intent);
    }
}
