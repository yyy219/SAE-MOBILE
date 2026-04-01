package com.openminds.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
    private LinearLayout sectionAdmin, sectionBenevole;
    private LinearLayout conteneurFormations;

    private UtilisateurViewModel utilisateurViewModel;
    private StatistiquesViewModel statistiquesViewModel;
    private FormationViewModel formationViewModel;

    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_espace);

        tvNomPrenom     = findViewById(R.id.tvNomPrenom);
        tvAvatar        = findViewById(R.id.tvAvatar);
        tvRole          = findViewById(R.id.tvRole);
        tvNiveau        = findViewById(R.id.tvNiveau);
        tvNbFormations  = findViewById(R.id.tvNbFormations);
        tvNbBadges      = findViewById(R.id.tvNbBadges);
        tvTauxReussite  = findViewById(R.id.tvTauxReussite);
        sectionAdmin    = findViewById(R.id.sectionAdmin);
        sectionBenevole = findViewById(R.id.sectionBenevole);

        utilisateurViewModel  = new ViewModelProvider(this).get(UtilisateurViewModel.class);
        statistiquesViewModel = new ViewModelProvider(this).get(StatistiquesViewModel.class);
        formationViewModel    = new ViewModelProvider(this).get(FormationViewModel.class);

        SharedPreferences prefs = getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("connected_user_id", -1);

        if (currentUserId == -1) {
            startActivity(new Intent(this, ConnexionActivity.class));
            finish();
            return;
        }

        utilisateurViewModel.getUtilisateurById(currentUserId).observe(this, utilisateur -> {
            if (utilisateur == null) return;

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
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                tvRole.setText("Bénévole · OpenMinds");
                if (sectionAdmin != null)    sectionAdmin.setVisibility(View.GONE);
                if (sectionBenevole != null) sectionBenevole.setVisibility(View.VISIBLE);
                chargerDonneesBenevole(prefs);
            }
        });


        // Navbar 3 onglets bénévole
        View navCatalogue = findViewById(R.id.navCatalogue);
        View navBadges    = findViewById(R.id.navBadges); // AJOUTE CECI

        if (navCatalogue != null)
            navCatalogue.setOnClickListener(v -> { startActivity(new Intent(this, CatalogueFormationsActivity.class)); finish(); });
        if (navBadges != null) // AJOUTE CECI
            navBadges.setOnClickListener(v -> { startActivity(new Intent(this, MesBadgesActivity.class)); finish(); });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchit les données à chaque retour sur la page
        if (currentUserId != -1 && conteneurFormations != null) {
            conteneurFormations.removeAllViews();
        }
    }

    // ─── STATS ADMIN ─────────────────────────────────────────────────────────

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

        View btnCreer    = findViewById(R.id.btnAdminCreerFormation);
        View btnStats    = findViewById(R.id.btnAdminStatistiques);
        View btnSess     = findViewById(R.id.btnAdminSessions);
        View btnDecoAdmin= findViewById(R.id.btnDeconnexionAdmin);

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

    // ─── DONNÉES BÉNÉVOLE ─────────────────────────────────────────────────────

    private void chargerDonneesBenevole(SharedPreferences prefs) {

        // 1. Stats vue d'ensemble depuis les inscriptions
        utilisateurViewModel.getMesInscriptions(currentUserId).observe(this, inscriptions -> {
            if (inscriptions == null) return;

            int nbFormations = inscriptions.size();
            long nbBadges    = inscriptions.stream()
                    .filter(i -> i.getProgressionPourcentage() >= 100).count();
            double moyenne   = inscriptions.stream()
                    .mapToInt(Inscription::getProgressionPourcentage)
                    .average().orElse(0);

            if (tvNbFormations != null) tvNbFormations.setText(String.valueOf(nbFormations));
            if (tvNbBadges     != null) tvNbBadges.setText(String.valueOf(nbBadges));
            if (tvTauxReussite != null) tvTauxReussite.setText(Math.round(moyenne) + "%");

            if (tvNiveau != null) {
                if      (nbBadges == 0) tvNiveau.setText("Niveau 1");
                else if (nbBadges <= 2) tvNiveau.setText("Niveau 2");
                else if (nbBadges <= 5) tvNiveau.setText("Niveau 3");
                else                    tvNiveau.setText("Niveau 4");
            }
        });

        // 2. Formations en cours — depuis la jointure BD (réelles)
        formationViewModel.getFormationsInscrites(currentUserId).observe(this, formations -> {

            // Récupérer le conteneur dynamique
            conteneurFormations = findViewById(R.id.conteneurFormationsBenevole);
            if (conteneurFormations == null) return;
            conteneurFormations.removeAllViews();

            TextView tvAucune = findViewById(R.id.tvAucuneFormation);

            if (formations == null || formations.isEmpty()) {
                // Aucune inscription → message
                if (tvAucune != null) tvAucune.setVisibility(View.VISIBLE);
                return;
            }
            if (tvAucune != null) tvAucune.setVisibility(View.GONE);

            // Croiser avec les inscriptions pour avoir la progression
            utilisateurViewModel.getMesInscriptions(currentUserId).observe(this, inscriptions -> {
                if (inscriptions == null) return;
                conteneurFormations.removeAllViews();

                int max = Math.min(formations.size(), 3);
                for (int i = 0; i < max; i++) {
                    Formation f = formations.get(i);
                    int progression = 0;
                    if (i < inscriptions.size())
                        progression = inscriptions.get(i).getProgressionPourcentage();
                    ajouterCarteFormation(f, progression);
                }

                // Badges dynamiques sous les formations
                afficherBadgesDynamiques(formations, inscriptions);
            });
        });

        // 3. Boutons
        View btnCatalogue = findViewById(R.id.btnBenevoleVoirCatalogue);
        View btnSession   = findViewById(R.id.btnBenevoleChoisirSession);
        View btnDecoBenv  = findViewById(R.id.btnDeconnexionBenevole);

        if (btnCatalogue != null)
            btnCatalogue.setOnClickListener(v ->
                    startActivity(new Intent(this, CatalogueFormationsActivity.class)));
        if (btnSession != null)
            btnSession.setOnClickListener(v ->
                    startActivity(new Intent(this, CatalogueFormationsActivity.class)));
        if (btnDecoBenv != null)
            btnDecoBenv.setOnClickListener(v -> deconnexion(prefs));
    }

    // ─── CARTE FORMATION DYNAMIQUE ────────────────────────────────────────────

    private void ajouterCarteFormation(Formation f, int progression) {
        LinearLayout carte = new LinearLayout(this);
        carte.setOrientation(LinearLayout.VERTICAL);
        carte.setPadding(dp(14), dp(12), dp(14), dp(12));
        carte.setBackgroundResource(R.drawable.bg_surface_card_light);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(8);
        carte.setLayoutParams(lp);

        // Chip thématique
        TextView chip = new TextView(this);
        chip.setText(capitaliser(f.getThematique()));
        chip.setTextSize(10f);
        chip.setPadding(dp(8), dp(3), dp(8), dp(3));
        chip.setBackgroundResource(R.drawable.bg_chip_env_light);
        chip.setTextColor(Color.parseColor("#0A7A6E"));
        LinearLayout.LayoutParams chipLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        chipLp.bottomMargin = dp(8);
        chip.setLayoutParams(chipLp);
        carte.addView(chip);

        // Ligne titre + %
        LinearLayout ligne = new LinearLayout(this);
        ligne.setOrientation(LinearLayout.HORIZONTAL);
        ligne.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams ligneLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ligneLp.bottomMargin = dp(8);
        ligne.setLayoutParams(ligneLp);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvTitre = new TextView(this);
        tvTitre.setText(f.getTitre());
        tvTitre.setTextSize(13f);
        tvTitre.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitre.setTextColor(Color.parseColor("#111827"));
        col.addView(tvTitre);

        TextView tvSous = new TextView(this);
        tvSous.setText(capitaliser(f.getThematique()) + " · " + f.getDureeMinutes() + " min");
        tvSous.setTextSize(11f);
        tvSous.setTextColor(Color.parseColor("#6B7280"));
        col.addView(tvSous);

        ligne.addView(col);

        TextView tvPct = new TextView(this);
        tvPct.setText(progression + "%");
        tvPct.setTextSize(13f);
        tvPct.setTypeface(null, android.graphics.Typeface.BOLD);
        tvPct.setTextColor(Color.parseColor("#0AAEA0"));
        LinearLayout.LayoutParams pctLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        pctLp.setMarginStart(dp(10));
        tvPct.setLayoutParams(pctLp);
        ligne.addView(tvPct);
        carte.addView(ligne);

        // ProgressBar
        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax(100);
        pb.setProgress(progression);
        pb.setBackgroundResource(R.drawable.bg_progress_track_light);
        pb.setProgressDrawable(getResources().getDrawable(R.drawable.bg_progress_fill, null));
        pb.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(4)));
        carte.addView(pb);

        conteneurFormations.addView(carte);
    }

    // ─── BADGES DYNAMIQUES ────────────────────────────────────────────────────

    private void afficherBadgesDynamiques(List<Formation> formations, List<Inscription> inscriptions) {
        LinearLayout conteneurBadges = findViewById(R.id.conteneurBadgesBenevole);
        LinearLayout sectionBadges  = findViewById(R.id.sectionBadgesBenevole);
        if (conteneurBadges == null || sectionBadges == null) return;

        conteneurBadges.removeAllViews();
        boolean auMoinsUnBadge = false;

        for (int i = 0; i < formations.size(); i++) {
            int progression = i < inscriptions.size()
                    ? inscriptions.get(i).getProgressionPourcentage() : 0;
            if (progression >= 100) {
                auMoinsUnBadge = true;
                ajouterBadge(conteneurBadges, formations.get(i).getTitre());
            }
        }

        sectionBadges.setVisibility(auMoinsUnBadge ? View.VISIBLE : View.GONE);
    }

    private void ajouterBadge(LinearLayout conteneur, String titre) {
        LinearLayout badge = new LinearLayout(this);
        badge.setOrientation(LinearLayout.VERTICAL);
        badge.setGravity(android.view.Gravity.CENTER);
        badge.setPadding(dp(10), dp(10), dp(10), dp(10));
        badge.setBackgroundResource(R.drawable.bg_surface_card_light);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        bp.setMarginEnd(dp(4));
        badge.setLayoutParams(bp);

        TextView etoile = new TextView(this);
        etoile.setText("★");
        etoile.setTextSize(22f);
        etoile.setTextColor(Color.parseColor("#0AAEA0"));
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ep.bottomMargin = dp(4);
        etoile.setLayoutParams(ep);
        badge.addView(etoile);

        TextView nom = new TextView(this);
        nom.setText(titre.length() > 14 ? titre.substring(0, 14) + "…" : titre);
        nom.setTextSize(10f);
        nom.setTypeface(null, android.graphics.Typeface.BOLD);
        nom.setTextColor(Color.parseColor("#111827"));
        nom.setGravity(android.view.Gravity.CENTER);
        badge.addView(nom);

        conteneur.addView(badge);
    }

    // ─── UTILITAIRES ──────────────────────────────────────────────────────────

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }

    private String capitaliser(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private void deconnexion(SharedPreferences prefs) {
        prefs.edit().clear().apply();
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ConnexionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}