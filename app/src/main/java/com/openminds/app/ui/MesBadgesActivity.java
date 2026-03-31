package com.openminds.app.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.openminds.R;
import com.openminds.app.viewmodel.FormationViewModel;
import com.openminds.app.viewmodel.UtilisateurViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MesBadgesActivity extends AppCompatActivity {

    private String nomCompletUtilisateur = "Utilisateur";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_badges);

        ImageView btnRetour = findViewById(R.id.btnRetourBadges);
        btnRetour.setOnClickListener(v -> finish()); // Ferme la page

        RecyclerView rvBadges = findViewById(R.id.rvBadges);
        rvBadges.setLayoutManager(new LinearLayoutManager(this));

        // 1. Initialiser l'Adapter et gérer le clic pour générer le PDF
        BadgeAdapter adapter = new BadgeAdapter(formation -> {
            String dateDuJour = new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE).format(new Date());
            // Appel de la méthode que tu as ajoutée dans PdfExportHelper
            PdfExportHelper.exportAttestationPdf(this, nomCompletUtilisateur, formation.getTitre(), dateDuJour);
        });
        rvBadges.setAdapter(adapter);

        // 2. Charger le nom du vrai utilisateur pour l'écrire sur le PDF
        SharedPreferences prefs = getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("connected_user_id", -1);

        UtilisateurViewModel userVM = new ViewModelProvider(this).get(UtilisateurViewModel.class);
        userVM.getUtilisateurById(userId).observe(this, u -> {
            if (u != null) {
                nomCompletUtilisateur = u.getPrenom() + " " + u.getNom();
            }
        });

        // 3. Charger toutes les formations de la base de données et les envoyer à la liste
        FormationViewModel formVM = new ViewModelProvider(this).get(FormationViewModel.class);

        formVM.toutesLesFormations.observe(this, formations -> {
            if (formations != null) {
                adapter.setFormations(formations);
            }
        });
    }
}