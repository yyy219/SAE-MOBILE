package com.openminds.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.app.database.entity.Formation;
import com.openminds.openminds.R;

import java.util.ArrayList;
import java.util.List;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.FormationViewHolder> {

    public interface OnFormationClickListener {
        void onFormationClick(Formation formation);
    }

    private List<Formation> formations = new ArrayList<>();
    private final OnFormationClickListener listener;

    public CatalogueAdapter(OnFormationClickListener listener) {
        this.listener = listener;
    }

    public void setFormations(List<Formation> formations) {
        this.formations = formations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FormationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_formation_catalogue, parent, false);
        return new FormationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FormationViewHolder holder, int position) {
        Formation formation = formations.get(position);
        holder.tvTitre.setText(formation.getTitre());
        holder.tvThematique.setText(formation.getThematique() != null ? formation.getThematique().toUpperCase() : "");
        holder.tvDuree.setText(formation.getDureeMinutes() + " min");
        holder.itemView.setOnClickListener(v -> listener.onFormationClick(formation));
    }

    @Override
    public int getItemCount() {
        return formations.size();
    }

    static class FormationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvThematique, tvDuree;

        FormationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitre = itemView.findViewById(R.id.tvTitreFormation);
            tvThematique = itemView.findViewById(R.id.tvThematiqueFormation);
            tvDuree = itemView.findViewById(R.id.tvDureeFormation);
        }
    }
}
