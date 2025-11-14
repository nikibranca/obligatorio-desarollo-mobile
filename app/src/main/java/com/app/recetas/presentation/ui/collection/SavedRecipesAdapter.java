package com.app.recetas.presentation.ui.collection;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.recetas.data.local.entities.Recipe;
// import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipesAdapter extends RecyclerView.Adapter<SavedRecipesAdapter.VH> {

    public interface OnRecipeAction {
        void onOpen(Recipe r);       // <-- NUEVO: abrir detalle
        void onEditNotes(Recipe r);
        void onDelete(Recipe r);
    }

    private final List<Recipe> data = new ArrayList<>();
    private final OnRecipeAction actions;

    public SavedRecipesAdapter(OnRecipeAction actions) {
        this.actions = actions;
    }

    public void submit(List<Recipe> recipes) {
        data.clear();
        if (recipes != null) data.addAll(recipes);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        int pad = (int)(12 * ctx.getResources().getDisplayMetrics().density);

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setPadding(pad, pad, pad, pad);
        root.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView img = new ImageView(ctx);
        int imgSize = (int)(72 * ctx.getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(imgSize, imgSize);
        imgLp.rightMargin = pad;
        img.setLayoutParams(imgLp);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        root.addView(img);

        LinearLayout col = new LinearLayout(ctx);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        root.addView(col);

        TextView title = new TextView(ctx);
        title.setTextSize(18f);
        col.addView(title);

        TextView meta = new TextView(ctx);
        meta.setTextSize(14f);
        col.addView(meta);

        TextView notes = new TextView(ctx);
        notes.setTextSize(13f);
        col.addView(notes);

        LinearLayout btns = new LinearLayout(ctx);
        btns.setOrientation(LinearLayout.VERTICAL);
        root.addView(btns);

        Button btnOpen = new Button(ctx);
        btnOpen.setText("Ver");              // <-- NUEVO
        btns.addView(btnOpen);

        Button btnNotes = new Button(ctx);
        btnNotes.setText("Notas");
        btns.addView(btnNotes);

        Button btnDelete = new Button(ctx);
        btnDelete.setText("Eliminar");
        btns.addView(btnDelete);

        return new VH(root, img, title, meta, notes, btnOpen, btnNotes, btnDelete);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Recipe r = data.get(position);

        h.title.setText(r.getName() != null ? r.getName() : "(Sin título)");

        StringBuilder sb = new StringBuilder();
        if (r.getCategory() != null && !r.getCategory().isEmpty()) sb.append("• ").append(r.getCategory()).append("  ");
        if (r.getArea() != null && !r.getArea().isEmpty()) sb.append("• ").append(r.getArea());
        h.meta.setText(sb.toString());

        String n = r.getPersonalNotes();
        h.notes.setText(n != null && !n.isEmpty() ? "📝 " + n : "");

        // Imagen si tenés Glide:
        // if (r.getImageUrl() != null && !r.getImageUrl().isEmpty()) {
        //     Glide.with(h.itemView.getContext()).load(r.getImageUrl()).into(h.img);
        // } else {
        //     h.img.setImageResource(android.R.drawable.ic_menu_report_image);
        // }

        // Clicks
        h.itemView.setOnClickListener(v -> actions.onOpen(r)); // abrir con tap al item
        h.btnOpen.setOnClickListener(v -> actions.onOpen(r));  // o con botón "Ver"
        h.btnNotes.setOnClickListener(v -> actions.onEditNotes(r));
        h.btnDelete.setOnClickListener(v -> actions.onDelete(r));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img; TextView title, meta, notes; Button btnOpen, btnNotes, btnDelete;
        VH(@NonNull View itemView, ImageView img, TextView title, TextView meta, TextView notes,
           Button btnOpen, Button btnNotes, Button btnDelete) {
            super(itemView);
            this.img = img; this.title = title; this.meta = meta; this.notes = notes;
            this.btnOpen = btnOpen; this.btnNotes = btnNotes; this.btnDelete = btnDelete;
        }
    }
}
