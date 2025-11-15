package com.app.recetas.presentation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.app.recetas.data.local.entities.Recipe;
import com.app.recetas.data.repository.RecipeRepository;

public class NewRecipeViewModel extends AndroidViewModel {

    private final RecipeRepository repository;

    public NewRecipeViewModel(@NonNull Application application) {
        super(application);
        // Usamos el mismo patrón que en tus otros ViewModels:
        // el repositorio recibe Application y se encarga de AppDatabase y RecipeDao.
        repository = new RecipeRepository(application);
    }

    /**
     * Guarda una receta personal (creada por el usuario) en la BD local.
     * Se ejecuta en background porque RecipeRepository ya usa ExecutorService.
     */
    public void savePersonalRecipe(Recipe recipe) {
        // Marcamos explícitamente como receta personal
        recipe.setPersonal(true);

        long now = System.currentTimeMillis();
        // En tu entidad Recipe los campos son dateAdded y dateModified:
        recipe.setDateAdded(now);
        recipe.setDateModified(now);

        repository.insertRecipe(recipe);
    }
}
