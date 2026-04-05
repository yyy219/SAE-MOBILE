package com.openminds.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.app.database.AppDatabase;
import com.openminds.app.database.entity.Contenu;
import com.openminds.app.database.entity.Inscription;
import com.openminds.openminds.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;

public class ContenuActivity extends AppCompatActivity {

    private final Set<Integer> modulesValides = new HashSet<>();
    private List<Contenu> tousLesModules = new ArrayList<>();
    private Button btnValider;
    private int formationId;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        formationId = getIntent().getIntExtra("formationId", -1);
        currentUserId = getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE)
                .getInt("connected_user_id", -1);

        TextView tvTitre     = findViewById(R.id.tv_titre_formation);
        TextView tvThematique = findViewById(R.id.tv_thematique);
        RecyclerView recycler = findViewById(R.id.recycler_modules);
        btnValider            = findViewById(R.id.btn_valider_formation);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        btnValider.setEnabled(false);
        btnValider.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#757575")));

        ArrayList<Contenu> liste = new ArrayList<>();
        ContenuAdapter adapter = new ContenuAdapter(liste, contenu -> {

            modulesValides.add(contenu.getId());
            Toast.makeText(this, "✓ " + contenu.getTitre() + " terminé !", Toast.LENGTH_SHORT).show();


            if (!tousLesModules.isEmpty() && modulesValides.containsAll(
                    tousLesModules.stream().map(Contenu::getId).collect(java.util.stream.Collectors.toSet()))) {
                btnValider.setEnabled(true);
                btnValider.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2ECC71")));
            }
        });
        recycler.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(getApplication());

        db.formationDao().getFormationById(formationId).observe(this, formation -> {
            if (formation != null) {
                tvTitre.setText(formation.getTitre());
                tvThematique.setText(formation.getThematique());
            }
        });

        db.contenuDao().getContenusByFormation(formationId).observe(this, contenus -> {
            liste.clear();
            tousLesModules.clear();
            if (contenus != null) {
                liste.addAll(contenus);
                tousLesModules.addAll(contenus);
            }
            adapter.notifyDataSetChanged();
        });

        btnValider.setOnClickListener(v -> validerFormation(db));
    }

    private void validerFormation(AppDatabase db) {
        if (currentUserId == -1 || formationId == -1) return;

        Executors.newSingleThreadExecutor().execute(() -> {

            List<com.openminds.app.database.entity.Session> sessions =
                    db.sessionDao().getSessionsByFormationSync(formationId);

            if (sessions == null || sessions.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Aucune session disponible", Toast.LENGTH_SHORT).show());
                return;
            }

            int sessionId = sessions.get(0).getId();


            int dejaInscrit = db.inscriptionDao().isDejaInscrit(currentUserId, sessionId);
            if (dejaInscrit > 0) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Vous êtes déjà inscrit à cette formation !", Toast.LENGTH_SHORT).show());
                return;
            }


            Inscription inscription = new Inscription();
            inscription.setUtilisateurId(currentUserId);
            inscription.setSessionId(sessionId);
            inscription.setDateInscription(
                    new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(new Date()));
            inscription.setStatut("validée");
            inscription.setProgressionPourcentage(100);
            inscription.setTimestampInscription(System.currentTimeMillis());
            db.inscriptionDao().insert(inscription);

            runOnUiThread(() -> {
                Toast.makeText(this, "Formation validée ! 🎉", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MonEspaceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        });
    }
}