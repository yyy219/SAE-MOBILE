package com.openminds.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.openminds.R;
import com.openminds.app.database.entity.Contenu;

import java.util.List;

public class ContenuAdapter extends RecyclerView.Adapter<ContenuAdapter.ContenuViewHolder> {

    private List<Contenu> listeModules;
    private OnModuleClickListener listener;

    // 1. Interface pour gérer le clic sur un module (indispensable pour ouvrir le cours)
    public interface OnModuleClickListener {
        void onModuleClick(Contenu contenu);
    }

    // 2. Le constructeur de ton Adapter
    public ContenuAdapter(List<Contenu> listeModules, OnModuleClickListener listener) {
        this.listeModules = listeModules;
        this.listener = listener;
    }

    // 3. Cette méthode "gonfle" (inflate) le fichier XML item_contenu pour créer la vue
    @NonNull
    @Override
    public ContenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contenu, parent, false);
        return new ContenuViewHolder(view);
    }

    // 4. C'est ICI qu'on injecte les données de la base dans les TextView de la carte
    @Override
    public void onBindViewHolder(@NonNull ContenuViewHolder holder, int position) {
        // On récupère le module (Contenu) correspondant à la position dans la liste
        Contenu moduleActuel = listeModules.get(position);

        // On remplit les textes avec l'attribut "ordre" et "titre" de la base
        holder.tvNumero.setText("Module " + moduleActuel.getOrdre());
        holder.tvTitre.setText(moduleActuel.getTitre());

        // On configure le clic sur la petite carte entière
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onModuleClick(moduleActuel);
            }
        });
    }

    // 5. Combien y a-t-il d'éléments dans la liste ?
    @Override
    public int getItemCount() {
        return listeModules != null ? listeModules.size() : 0;
    }

    // 6. La classe interne qui "tient" en mémoire les vues de ton item_contenu.xml
    static class ContenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumero;
        TextView tvTitre;

        public ContenuViewHolder(@NonNull View itemView) {
            super(itemView);
            // On relie les variables Java aux ID de ton fichier XML
            tvNumero = itemView.findViewById(R.id.tv_module_numero);
            tvTitre = itemView.findViewById(R.id.tv_module_titre);
        }
    }
}
