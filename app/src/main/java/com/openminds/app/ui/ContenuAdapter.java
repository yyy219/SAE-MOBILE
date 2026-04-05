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


    public interface OnModuleClickListener {
        void onModuleClick(Contenu contenu);
    }


    public ContenuAdapter(List<Contenu> listeModules, OnModuleClickListener listener) {
        this.listeModules = listeModules;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ContenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contenu, parent, false);
        return new ContenuViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ContenuViewHolder holder, int position) {

        Contenu moduleActuel = listeModules.get(position);


        holder.tvNumero.setText("Module " + moduleActuel.getOrdre());
        holder.tvTitre.setText(moduleActuel.getTitre());


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onModuleClick(moduleActuel);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listeModules != null ? listeModules.size() : 0;
    }


    static class ContenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumero;
        TextView tvTitre;

        public ContenuViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNumero = itemView.findViewById(R.id.tv_module_numero);
            tvTitre = itemView.findViewById(R.id.tv_module_titre);
        }
    }
}
