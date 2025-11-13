package com.app.recetas.presentation.ui.search.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.recetas.data.remote.dto.MealDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para mostrar resultados de búsqueda de recetas
 * Permite ver detalle y agregar a colección
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {
    
    private List<MealDto> meals = new ArrayList<>();
    private OnAddToCollectionListener addListener;
    private OnRecipeClickListener clickListener;
    
    /**
     * Interface para callback cuando se agrega receta a colección
     */
    public interface OnAddToCollectionListener {
        void onAddToCollection(MealDto meal);
    }
    
    /**
     * Interface para callback cuando se hace clic en una receta
     */
    public interface OnRecipeClickListener {
        void onRecipeClick(MealDto meal);
    }
    
    /**
     * Constructor del adapter
     * @param addListener Callback para agregar a colección
     * @param clickListener Callback para ver detalle
     */
    public SearchResultAdapter(OnAddToCollectionListener addListener, OnRecipeClickListener clickListener) {
        this.addListener = addListener;
        this.clickListener = clickListener;
    }
    
    /**
     * Constructor con solo callback de agregar (para compatibilidad)
     */
    public SearchResultAdapter(OnAddToCollectionListener addListener) {
        this.addListener = addListener;
        this.clickListener = null;
    }
    
    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = createItemView(parent);
        return new SearchResultViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        MealDto meal = meals.get(position);
        holder.bind(meal);

    }
    
    @Override
    public int getItemCount() {
        return meals.size();
    }
    
    public void setMeals(List<MealDto> newMeals) {
        this.meals = newMeals != null ? newMeals : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    /**
     * Crea la vista de cada item programáticamente
     */
    private View createItemView(ViewGroup parent) {
        LinearLayout itemLayout = new LinearLayout(parent.getContext());
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(20, 15, 20, 15);
        itemLayout.setClickable(true);
        itemLayout.setFocusable(true);
        
        // Agregar borde visual y efecto de clic
        itemLayout.setBackgroundColor(0xFFF5F5F5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 5, 10, 5);
        itemLayout.setLayoutParams(layoutParams);
        
        // Nombre de la receta
        TextView textName = new TextView(parent.getContext());
        textName.setId(View.generateViewId());
        textName.setTextSize(18);
        textName.setTextColor(0xFF000000);
        itemLayout.addView(textName);
        
        // Categoría y área
        TextView textDetails = new TextView(parent.getContext());
        textDetails.setId(View.generateViewId());
        textDetails.setTextSize(14);
        textDetails.setTextColor(0xFF666666);
        textDetails.setPadding(0, 5, 0, 10);
        itemLayout.addView(textDetails);
        
        // Instrucciones (preview)
        TextView textInstructions = new TextView(parent.getContext());
        textInstructions.setId(View.generateViewId());
        textInstructions.setTextSize(12);
        textInstructions.setTextColor(0xFF888888);
        textInstructions.setMaxLines(2);
        textInstructions.setPadding(0, 0, 0, 10);
        itemLayout.addView(textInstructions);
        
        // Layout para botones
        LinearLayout buttonLayout = new LinearLayout(parent.getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        // Botón ver detalle
        Button btnDetail = new Button(parent.getContext());
        btnDetail.setId(View.generateViewId());
        btnDetail.setText("👁️ Ver Receta");
        btnDetail.setTextSize(12);
        btnDetail.setLayoutParams(new LinearLayout.LayoutParams(0, 
            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        buttonLayout.addView(btnDetail);
        
        // Botón agregar
        Button btnAdd = new Button(parent.getContext());
        btnAdd.setId(View.generateViewId());
        btnAdd.setText("➕ Agregar");
        btnAdd.setTextSize(12);
        btnAdd.setLayoutParams(new LinearLayout.LayoutParams(0, 
            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        buttonLayout.addView(btnAdd);
        
        itemLayout.addView(buttonLayout);
        
        return itemLayout;
    }
    
    /**
     * ViewHolder para cada item de receta
     */
    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        
        private TextView textName, textDetails, textInstructions;
        private Button btnDetail, btnAdd;
        private LinearLayout itemLayout;
        
        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // Encontrar vistas por ID
            itemLayout = (LinearLayout) itemView;
            textName = (TextView) itemLayout.getChildAt(0);
            textDetails = (TextView) itemLayout.getChildAt(1);
            textInstructions = (TextView) itemLayout.getChildAt(2);
            
            LinearLayout buttonLayout = (LinearLayout) itemLayout.getChildAt(3);
            btnDetail = (Button) buttonLayout.getChildAt(0);
            btnAdd = (Button) buttonLayout.getChildAt(1);
        }
        
        /**
         * Vincula los datos de la receta con las vistas
         */
        public void bind(MealDto meal) {
            // Nombre de la receta
            textName.setText(meal.strMeal != null ? meal.strMeal : "Sin nombre");
            
            // Categoría y área
            String details = "";
            if (meal.strCategory != null && !meal.strCategory.isEmpty()) {
                details += "📂 " + meal.strCategory;
            }
            if (meal.strArea != null && !meal.strArea.isEmpty()) {
                if (!details.isEmpty()) details += " • ";
                details += "🌍 " + meal.strArea;
            }
            textDetails.setText(details.isEmpty() ? "Sin categoría" : details);
            
            // Preview de instrucciones
            if (meal.strInstructions != null && !meal.strInstructions.isEmpty()) {
                String preview = meal.strInstructions.length() > 80 ? 
                    meal.strInstructions.substring(0, 80) + "..." : 
                    meal.strInstructions;
                textInstructions.setText("📝 " + preview);
            } else {
                textInstructions.setText("📝 Sin instrucciones disponibles");
            }
            
            // Configurar botón ver detalle
            btnDetail.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onRecipeClick(meal);
                }
            });
            
            // Configurar clic en todo el item para ver detalle
            itemLayout.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onRecipeClick(meal);
                }
            });
            
            // Configurar botón agregar
            btnAdd.setOnClickListener(v -> {
                if (addListener != null) {
                    addListener.onAddToCollection(meal);
                    
                    // Cambiar texto del botón temporalmente
                    btnAdd.setText("✅ Agregado");
                    btnAdd.setEnabled(false);
                    
                    // Restaurar botón después de 2 segundos
                    btnAdd.postDelayed(() -> {
                        btnAdd.setText("➕ Agregar");
                        btnAdd.setEnabled(true);
                    }, 2000);
                }
            });
        }
    }
}
