package com.openminds.app.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.app.database.entity.Session;
import com.openminds.openminds.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    public interface OnSessionSelectListener {
        void onSessionSelected(Session session, boolean estComplet);
    }

    private List<Session> sessions = new ArrayList<>();
    private final Context context;
    private final OnSessionSelectListener listener;
    private int selectedPosition = -1;
    private Map<Integer, Integer> inscritsMap = new HashMap<>();

    public SessionAdapter(Context context, OnSessionSelectListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    public void setInscritsMap(Map<Integer, Integer> map) {
        this.inscritsMap = map;
        notifyDataSetChanged();
    }

    public Session getSelectedSession() {
        if (selectedPosition >= 0 && selectedPosition < sessions.size()) {
            return sessions.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessions.get(position);

        int inscrits = inscritsMap.getOrDefault(session.getId(), 0);
        int placesMax = session.getPlacesMax();
        boolean estComplet = inscrits >= placesMax;
        int placesDisponibles = placesMax - inscrits;

        String type = session.getType() != null ? session.getType() : "enligne";
        boolean isPresentielle = type.equals("presentielle");
        holder.tvNomSession.setText(isPresentielle ? "Présentielle" : "En Ligne");

        String date = session.getDateDebut() + " – " + session.getDateFin();
        holder.tvDateSession.setText(date);

        if (estComplet) {
            holder.tvPlacesSession.setText("✗ Complet");
            holder.tvPlacesSession.setTextColor(Color.parseColor("#E74C3C"));
        } else {
            holder.tvPlacesSession.setText("● " + placesDisponibles + " places disponibles");
            holder.tvPlacesSession.setTextColor(Color.parseColor("#2ECC71"));
        }

        if (isPresentielle) {
            holder.ivSession.setImageResource(android.R.drawable.ic_menu_mylocation);
            holder.tvLieuBadge.setText("📍 " + (session.getLieu() != null ? session.getLieu() : "Présentiel"));
        } else {
            holder.ivSession.setImageResource(android.R.drawable.ic_menu_share);
            holder.tvLieuBadge.setText("🖥 En ligne");
        }

        holder.tvDureeBadge.setText("🕐 1h");
        holder.tvMaxBadge.setText("👥 " + placesMax + " max");

        holder.radioSession.setChecked(position == selectedPosition);
        holder.itemView.setAlpha(estComplet ? 0.5f : 1.0f);

        holder.itemView.setOnClickListener(v -> {
            if (!estComplet) {
                int anciennePos = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(anciennePos);
                notifyItemChanged(selectedPosition);
                listener.onSessionSelected(session, false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSession;
        TextView tvNomSession, tvDateSession, tvPlacesSession;
        TextView tvLieuBadge, tvDureeBadge, tvMaxBadge;
        RadioButton radioSession;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSession = itemView.findViewById(R.id.ivSession);
            tvNomSession = itemView.findViewById(R.id.tvNomSession);
            tvDateSession = itemView.findViewById(R.id.tvDateSession);
            tvPlacesSession = itemView.findViewById(R.id.tvPlacesSession);
            tvLieuBadge = itemView.findViewById(R.id.tvLieuBadge);
            tvDureeBadge = itemView.findViewById(R.id.tvDureeBadge);
            tvMaxBadge = itemView.findViewById(R.id.tvMaxBadge);
            radioSession = itemView.findViewById(R.id.radioSession);
        }
    }
}