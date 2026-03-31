package com.openminds.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.openminds.openminds.R;
import com.openminds.app.database.entity.Formation;
import java.util.ArrayList;
import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder> {
    private List<Formation> formations = new ArrayList<>();
    private final OnBadgeClickListener listener;

    public interface OnBadgeClickListener {
        void onDownloadClick(Formation formation);
    }

    public BadgeAdapter(OnBadgeClickListener listener) {
        this.listener = listener;
    }

    public void setFormations(List<Formation> formations) {
        this.formations = formations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badge, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        Formation formation = formations.get(position);
        holder.tvTitre.setText(formation.getTitre());
        // Quand on clique sur le bouton PDF, on envoie la formation sélectionnée à l'Activity
        holder.btnDownload.setOnClickListener(v -> listener.onDownloadClick(formation));
    }

    @Override
    public int getItemCount() {
        return formations.size();
    }

    static class BadgeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre;
        Button btnDownload;

        BadgeViewHolder(View itemView) {
            super(itemView);
            tvTitre = itemView.findViewById(R.id.tvTitreBadge);
            btnDownload = itemView.findViewById(R.id.btnDownloadBadge);
        }
    }
}