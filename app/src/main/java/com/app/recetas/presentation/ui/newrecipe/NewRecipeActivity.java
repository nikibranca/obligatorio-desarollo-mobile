package com.app.recetas.presentation.ui.newrecipe;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.app.recetas.data.local.entities.Recipe;
import com.app.recetas.presentation.viewmodel.NewRecipeViewModel;
import com.app.recetas.utils.InputValidator;
import com.app.recetas.utils.PreferencesManager;

import java.util.UUID;

public class NewRecipeActivity extends AppCompatActivity {

    private EditText inputName;
    private EditText inputCategory;
    private EditText inputArea;
    private EditText inputIngredients;
    private EditText inputInstructions;
    private EditText inputNotes;
    private TextView textStatus;

    private NewRecipeViewModel viewModel;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NewRecipeViewModel.class);
        preferencesManager = new PreferencesManager(this);

        setContentView(buildContentView());
    }

    private ScrollView buildContentView() {
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Título
        TextView title = new TextView(this);
        title.setText("➕ Nueva receta personal");
        title.setTextSize(22);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, 0, 0, dpToPx(16));
        title.setLayoutParams(titleParams);
        root.addView(title);

        // Nombre
        inputName = createLabeledEditText(root, "Nombre de la receta");

        // Categoría (ej: Postre, Carne, Vegetariano...)
        inputCategory = createLabeledEditText(root, "Categoría (ej: Postre, Carne, Vegetariano)");

        // Área (ej: Italiana, Mexicana...)
        inputArea = createLabeledEditText(root, "Área / Origen (ej: Italiana, Mexicana)");

        // Ingredientes (multilínea)
        inputIngredients = createLabeledMultiEditText(root, "Ingredientes (uno por línea)");

        // Instrucciones (multilínea)
        inputInstructions = createLabeledMultiEditText(root, "Instrucciones");

        // Notas personales (opcional)
        inputNotes = createLabeledMultiEditText(root, "Notas personales (opcional)");

        // TextStatus
        textStatus = new TextView(this);
        textStatus.setText("");
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        statusParams.setMargins(0, dpToPx(12), 0, dpToPx(12));
        textStatus.setLayoutParams(statusParams);
        root.addView(textStatus);

        // Botón guardar
        Button btnSave = new Button(this);
        btnSave.setText("Guardar receta");
        btnSave.setAllCaps(false);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        btnParams.setMargins(0, dpToPx(16), 0, dpToPx(8));
        btnSave.setLayoutParams(btnParams);
        root.addView(btnSave);

        // Botón cancelar
        Button btnCancel = new Button(this);
        btnCancel.setText("Cancelar");
        btnCancel.setAllCaps(false);
        root.addView(btnCancel);

        btnSave.setOnClickListener(v -> onSaveClicked());
        btnCancel.setOnClickListener(v -> finish());

        scroll.addView(root);
        return scroll;
    }

    private EditText createLabeledEditText(LinearLayout parent, String labelText) {
        TextView label = new TextView(this);
        label.setText(labelText);
        label.setTextSize(16);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        labelParams.setMargins(0, dpToPx(8), 0, dpToPx(4));
        label.setLayoutParams(labelParams);
        parent.addView(label);

        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        editText.setLayoutParams(editParams);
        parent.addView(editText);

        return editText;
    }

    private EditText createLabeledMultiEditText(LinearLayout parent, String labelText) {
        TextView label = new TextView(this);
        label.setText(labelText);
        label.setTextSize(16);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        labelParams.setMargins(0, dpToPx(8), 0, dpToPx(4));
        label.setLayoutParams(labelParams);
        parent.addView(label);

        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMinLines(3);
        editText.setMaxLines(8);
        editText.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        editText.setLayoutParams(editParams);
        parent.addView(editText);

        return editText;
    }

    private void onSaveClicked() {
        String name = inputName.getText().toString().trim();
        String category = inputCategory.getText().toString().trim();
        String area = inputArea.getText().toString().trim();
        String ingredients = inputIngredients.getText().toString().trim();
        String instructions = inputInstructions.getText().toString().trim();
        String notes = inputNotes.getText().toString().trim();

        // VALIDACIONES usando métodos estáticos de InputValidator
        InputValidator.ValidationResult vName = InputValidator.validateRecipeName(name);
        if (vName.hasError()) {
            showError(vName.errorMessage);
            return;
        }

        InputValidator.ValidationResult vCategory = InputValidator.validateCategory(category);
        if (vCategory.hasError()) {
            showError(vCategory.errorMessage);
            return;
        }

        InputValidator.ValidationResult vArea = InputValidator.validateArea(area);
        if (vArea.hasError()) {
            showError(vArea.errorMessage);
            return;
        }

        InputValidator.ValidationResult vIngredients = InputValidator.validateIngredients(ingredients);
        if (vIngredients.hasError()) {
            showError(vIngredients.errorMessage);
            return;
        }

        InputValidator.ValidationResult vInstructions = InputValidator.validateInstructions(instructions);
        if (vInstructions.hasError()) {
            showError(vInstructions.errorMessage);
            return;
        }

        // Notas personales: tu validador ya contempla que sean opcionales
        InputValidator.ValidationResult vNotes = InputValidator.validatePersonalNotes(notes);
        if (vNotes.hasError()) {
            showError(vNotes.errorMessage);
            return;
        }

        // --- Si llegamos hasta acá, todo es válido ---
        Recipe recipe = new Recipe();

        String id = java.util.UUID.randomUUID().toString();
        recipe.setId(id);

        recipe.setName(name);
        recipe.setCategory(category);
        recipe.setArea(area);
        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);
        recipe.setPersonalNotes(notes);
        recipe.setPersonal(true);
        recipe.setImageUrl(null);

        long now = System.currentTimeMillis();
        recipe.setDateAdded(now);
        recipe.setDateModified(now);

        viewModel.savePersonalRecipe(recipe);

        preferencesManager.saveLastRecipe(id, name);

        Toast.makeText(this, "Receta guardada correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void showError(String message) {
        textStatus.setText("❌ " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}
