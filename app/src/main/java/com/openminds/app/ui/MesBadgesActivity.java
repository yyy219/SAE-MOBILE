package com.openminds.app.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());

        RecyclerView rvBadges = findViewById(R.id.rvBadges);
        rvBadges.setLayoutManager(new LinearLayoutManager(this));

        BadgeAdapter adapter = new BadgeAdapter(formation -> {
            String dateDuJour = new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE).format(new Date());
            PdfExportHelper.exportAttestationPdf(this, nomCompletUtilisateur, formation.getTitre(), dateDuJour);
        });
        rvBadges.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("connected_user_id", -1);

        UtilisateurViewModel userVM = new ViewModelProvider(this).get(UtilisateurViewModel.class);
        userVM.getUtilisateurById(userId).observe(this, u -> {
            if (u != null) {
                nomCompletUtilisateur = u.getPrenom() + " " + u.getNom();
            }
        });

        FormationViewModel formVM = new ViewModelProvider(this).get(FormationViewModel.class);
        formVM.toutesLesFormations.observe(this, formations -> {
            if (formations != null) {
                adapter.setFormations(formations);
            }
        });
    }
}