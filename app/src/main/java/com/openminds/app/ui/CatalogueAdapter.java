package com.openminds.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.app.database.entity.Formation;
import com.openminds.openminds.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.FormationViewHolder> {


    public interface OnFormationClickListener {
        void onFormationClick(Formation formation);
    }

    public interface OnTelechargerClickListener {
        void onTelechargerClick(Formation formation, boolean dejaTelechargee);
    }


    private List<Formation> formations = new ArrayList<>();
    private Set<Integer> formationsTelechargees = new HashSet<>();
    private final OnFormationClickListener listener;
    private OnTelechargerClickListener telechargerListener;


    public CatalogueAdapter(OnFormationClickListener listener) {
        this.listener = listener;
    }


    public void setTelechargerListener(OnTelechargerClickListener telechargerListener) {
        this.telechargerListener = telechargerListener;
    }

    public void setFormations(List<Formation> formations) {
        this.formations = formations;
        notifyDataSetChanged();
    }

    /** Met à jour les IDs déjà téléchargés → rafraîchit l'icône du bouton en temps réel */
    public void setFormationsTelechargees(Set<Integer> ids) {
        this.formationsTelechargees = ids;
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
        holder.tvThematique.setText(
                formation.getThematique() != null
                        ? formation.getThematique().toUpperCase()
                        : ""
        );
        holder.tvDuree.setText(formation.getDureeMinutes() + " min");


        boolean dejaTelechargee = formationsTelechargees.contains(formation.getId());
        holder.btnTelecharger.setImageResource(
                dejaTelechargee ? R.drawable.ic_download_done : R.drawable.ic_download
        );
        holder.btnTelecharger.setContentDescription(
                dejaTelechargee ? "Supprimer le téléchargement" : "Télécharger la formation"
        );


        holder.btnTelecharger.setOnClickListener(v -> {
            if (telechargerListener != null) {
                telechargerListener.onTelechargerClick(formation, dejaTelechargee);
            }
        });


        holder.itemView.setOnClickListener(v -> listener.onFormationClick(formation));
    }

    @Override
    public int getItemCount() {
        return formations.size();
    }


    static class FormationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvThematique, tvDuree;
        ImageButton btnTelecharger;

        FormationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitre        = itemView.findViewById(R.id.tvTitreFormation);
            tvThematique   = itemView.findViewById(R.id.tvThematiqueFormation);
            tvDuree        = itemView.findViewById(R.id.tvDureeFormation);
            btnTelecharger = itemView.findViewById(R.id.btnTelecharger);
        }
    }
}