package com.openminds.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.openminds.openminds.R;
import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Inscription;
import com.openminds.app.viewmodel.FormationViewModel;
import com.openminds.app.viewmodel.StatistiquesViewModel;
import com.openminds.app.viewmodel.UtilisateurViewModel;

import java.util.List;

public class MonEspaceActivity extends AppCompatActivity {

    private TextView tvNomPrenom, tvAvatar, tvRole, tvNiveau;
    private TextView tvNbFormations, tvNbBadges, tvTauxReussite;
    private ImageView btnRetour, btnParametres;
    private LinearLayout sectionAdmin, sectionBenevole;

    private UtilisateurViewModel utilisateurViewModel;
    private StatistiquesViewModel statistiquesViewModel;
    private FormationViewModel formationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_espace);

        // Bind
        tvNomPrenom     = findViewById(R.id.tvNomPrenom);
        tvAvatar        = findViewById(R.id.tvAvatar);
        tvRole          = findViewById(R.id.tvRole);
        tvNiveau        = findViewById(R.id.tvNiveau);
        tvNbFormations  = findViewById(R.id.tvNbFormations);
        tvNbBadges      = findViewById(R.id.tvNbBadges);
        tvTauxReussite  = findViewById(R.id.tvTauxReussite);
        btnRetour       = findViewById(R.id.btnRetour);
        btnParametres   = findViewById(R.id.btnParametres);
        sectionAdmin    = findViewById(R.id.sectionAdmin);
        sectionBenevole = findViewById(R.id.sectionBenevole);

        // ViewModels
        utilisateurViewModel  = new ViewModelProvider(this).get(UtilisateurViewModel.class);
        statistiquesViewModel = new ViewModelProvider(this).get(StatistiquesViewModel.class);
        formationViewModel    = new ViewModelProvider(this).get(FormationViewModel.class);

        // Session
        SharedPreferences prefs = getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("connected_user_id", -1);

        if (userId == -1) {
            startActivity(new Intent(this, ConnexionActivity.class));
            finish();
            return;
        }

        // Charger utilisateur
        utilisateurViewModel.getUtilisateurById(userId).observe(this, utilisateur -> {
            if (utilisateur == null) return;

            // Nom + initiales
            String nomComplet = utilisateur.getPrenom() + " " + utilisateur.getNom();
            tvNomPrenom.setText(nomComplet);

            String initiales = "";
            if (utilisateur.getPrenom() != null && !utilisateur.getPrenom().isEmpty())
                initiales += utilisateur.getPrenom().substring(0, 1).toUpperCase();
            if (utilisateur.getNom() != null && !utilisateur.getNom().isEmpty())
                initiales += utilisateur.getNom().substring(0, 1).toUpperCase();
            tvAvatar.setText(initiales);

            String role = utilisateur.getRole();

            if ("admin".equals(role)) {
                // Admin → redirige directement vers le dashboard admin (MainActivity)
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                tvRole.setText("Bénévole · OpenMinds");
                if (sectionAdmin != null)    sectionAdmin.setVisibility(View.GONE);
                if (sectionBenevole != null) sectionBenevole.setVisibility(View.VISIBLE);
                chargerStatsBenevole(userId);
            }
        });

        // Boutons
        if (btnRetour != null)     btnRetour.setOnClickListener(v -> finish());
        if (btnParametres != null) btnParametres.setOnClickListener(v -> deconnexion(prefs));

        // Navbar
        View navAccueil   = findViewById(R.id.navAccueil);
        View navCatalogue = findViewById(R.id.navCatalogue);
        if (navAccueil != null)
            navAccueil.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        if (navCatalogue != null)
            navCatalogue.setOnClickListener(v -> startActivity(new Intent(this, CatalogueFormationsActivity.class)));
    }

    // ─── ADMIN ───────────────────────────────────────────────────────────────

    private void chargerStatsAdmin() {
        statistiquesViewModel.nbFormations.observe(this, nb -> {
            if (nb != null && tvNbFormations != null)
                tvNbFormations.setText(String.valueOf(nb));
        });
        statistiquesViewModel.nbBenevolesActifs.observe(this, nb -> {
            if (nb != null && tvNbBadges != null)
                tvNbBadges.setText(String.valueOf(nb));
        });
        statistiquesViewModel.tauxReussite.observe(this, taux -> {
            if (tvTauxReussite == null) return;
            tvTauxReussite.setText(taux != null ? Math.round(taux) + "%" : "—");
        });

        View btnCreer   = findViewById(R.id.btnAdminCreerFormation);
        View btnStats   = findViewById(R.id.btnAdminStatistiques);
        View btnSess    = findViewById(R.id.btnAdminSessions);
        View btnDecoAdmin = findViewById(R.id.btnDeconnexionAdmin);

        if (btnCreer != null)
            btnCreer.setOnClickListener(v -> startActivity(new Intent(this, NouvelleFormationActivity.class)));
        if (btnStats != null)
            btnStats.setOnClickListener(v -> startActivity(new Intent(this, StatistiquesActivity.class)));
        if (btnSess != null)
            btnSess.setOnClickListener(v -> startActivity(new Intent(this, ChoisirSessionActivity.class)));
        if (btnDecoAdmin != null)
            btnDecoAdmin.setOnClickListener(v -> deconnexion(
                    getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE)));
    }

    // ─── BÉNÉVOLE ─────────────────────────────────────────────────────────────

    private void chargerStatsBenevole(int userId) {
        // Observer inscriptions → stats + cartes
        utilisateurViewModel.getMesInscriptions(userId).observe(this, inscriptions -> {
            if (inscriptions == null) return;

            // Compteurs
            if (tvNbFormations != null)
                tvNbFormations.setText(String.valueOf(inscriptions.size()));

            long badges = inscriptions.stream()
                    .filter(i -> i.getProgressionPourcentage() >= 100).count();
            if (tvNbBadges != null)
                tvNbBadges.setText(String.valueOf(badges));

            double moyenne = inscriptions.stream()
                    .mapToInt(Inscription::getProgressionPourcentage)
                    .average().orElse(0);
            if (tvTauxReussite != null)
                tvTauxReussite.setText(Math.round(moyenne) + "%");

            // Niveau dynamique selon nb formations terminées
            calculerNiveau((int) badges);

            // Cartes formations dynamiques
            afficherFormationsEnCours(inscriptions);
        });

        // Boutons bénévole
        View btnCatalogue = findViewById(R.id.btnBenevoleVoirCatalogue);
        View btnSession   = findViewById(R.id.btnBenevoleChoisirSession);
        View btnDecoBenv  = findViewById(R.id.btnDeconnexionBenevole);

        if (btnCatalogue != null)
            btnCatalogue.setOnClickListener(v -> startActivity(new Intent(this, CatalogueFormationsActivity.class)));
        if (btnSession != null)
            btnSession.setOnClickListener(v -> startActivity(new Intent(this, ChoisirSessionActivity.class)));
        if (btnDecoBenv != null)
            btnDecoBenv.setOnClickListener(v -> deconnexion(
                    getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE)));
    }

    private void calculerNiveau(int nbBadges) {
        if (tvNiveau == null) return;
        if (nbBadges == 0)      tvNiveau.setText("Niveau 1");
        else if (nbBadges <= 2) tvNiveau.setText("Niveau 2");
        else if (nbBadges <= 5) tvNiveau.setText("Niveau 3");
        else                    tvNiveau.setText("Niveau 4");
    }

    private void afficherFormationsEnCours(List<Inscription> inscriptions) {
        // Observer toutes les formations pour croiser avec les inscriptions
        formationViewModel.toutesLesFormations.observe(this, formations -> {
            if (formations == null || inscriptions.isEmpty()) return;

            // Carte 1
            if (inscriptions.size() >= 1) {
                Inscription i1 = inscriptions.get(0);
                afficherCarte(
                        formations, i1,
                        R.id.tvTitreFormation1,
                        R.id.tvSousInfoFormation1,
                        R.id.progressFormation1,
                        R.id.tvPct1
                );
            }

            // Carte 2
            if (inscriptions.size() >= 2) {
                Inscription i2 = inscriptions.get(1);
                afficherCarte(
                        formations, i2,
                        R.id.tvTitreFormation2,
                        R.id.tvSousInfoFormation2,
                        R.id.progressFormation2,
                        R.id.tvPct2
                );
            }
        });
    }

    private void afficherCarte(List<Formation> formations, Inscription inscription,
                               int idTitre, int idSousInfo, int idProgress, int idPct) {
        // Trouver la formation correspondant à l'inscription via sessionId
        // (on cherche dans les formations disponibles)
        TextView tvTitre    = findViewById(idTitre);
        TextView tvSousInfo = findViewById(idSousInfo);
        ProgressBar pb      = findViewById(idProgress);
        TextView tvPct      = findViewById(idPct);

        int pct = inscription.getProgressionPourcentage();
        if (pb != null)     pb.setProgress(pct);
        if (tvPct != null)  tvPct.setText(pct + "%");

        // Si on trouve une formation dans la liste, on affiche son titre
        // Sinon on affiche un label générique
        if (!formations.isEmpty()) {
            Formation f = formations.get(0); // best effort — sera amélioré avec jointure
            if (tvTitre != null)    tvTitre.setText(f.getTitre());
            if (tvSousInfo != null) tvSousInfo.setText(f.getThematique() + " · " + f.getDureeMinutes() + " min");
        }
    }

    // ─── DÉCONNEXION ──────────────────────────────────────────────────────────

    private void deconnexion(SharedPreferences prefs) {
        prefs.edit().clear().apply();
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ConnexionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}