package com.openminds.app.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.openminds.openminds.R;
import com.openminds.app.database.entity.FormationTop;
import com.openminds.app.database.entity.StatThematique;
import com.openminds.app.viewmodel.StatistiquesViewModel;
import java.util.List;

public class StatistiquesActivity extends AppCompatActivity {

    private StatistiquesViewModel viewModel;

    // KPI Cards
    private TextView tvNbFormations, tvNbBenevoles, tvTauxReussite, tvNbSessions;

    // Label période
    private TextView tvLabelPeriode;

    // Thématiques
    private TextView tvPctInclusion, tvPctEnvironnement, tvPctEgalite;
    private ProgressBar pbInclusion, pbEnvironnement, pbEgalite;

    // Top formations
    private TextView tvTop1Titre, tvTop1Nb, tvTop2Titre, tvTop2Nb;

    // Sélecteur période
    private TextView btn7j, btnMois, btnAnnee;
    private TextView selectedPeriod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistiques);

        lierVues();
        viewModel = new ViewModelProvider(this).get(StatistiquesViewModel.class);
        setupPeriodeSelector();
        observerDonnees();
        configurerBoutonPdf();
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    // ── Liaison vues / variables ──────────────────────────────────

    private void lierVues() {
        tvNbFormations     = findViewById(R.id.tvNbFormations);
        tvNbBenevoles      = findViewById(R.id.tvNbBenevoles);
        tvTauxReussite     = findViewById(R.id.tvTauxReussite);
        tvNbSessions       = findViewById(R.id.tvNbSessions);
        tvLabelPeriode     = findViewById(R.id.tvLabelPeriode);

        tvPctInclusion     = findViewById(R.id.tvPctInclusion);
        tvPctEnvironnement = findViewById(R.id.tvPctEnvironnement);
        tvPctEgalite       = findViewById(R.id.tvPctEgalite);
        pbInclusion        = findViewById(R.id.pbInclusion);
        pbEnvironnement    = findViewById(R.id.pbEnvironnement);
        pbEgalite          = findViewById(R.id.pbEgalite);

        tvTop1Titre = findViewById(R.id.tvTop1Titre);
        tvTop1Nb    = findViewById(R.id.tvTop1Nb);
        tvTop2Titre = findViewById(R.id.tvTop2Titre);
        tvTop2Nb    = findViewById(R.id.tvTop2Nb);

        btn7j    = findViewById(R.id.btn7Jours);
        btnMois  = findViewById(R.id.btnCeMois);
        btnAnnee = findViewById(R.id.btnAnnee);
    }

    // ── Observation des LiveData ──────────────────────────────────

    private void observerDonnees() {

        viewModel.nbFormations.observe(this, n ->
                tvNbFormations.setText(String.valueOf(n != null ? n : 0)));

        viewModel.nbBenevolesActifs.observe(this, n ->
                tvNbBenevoles.setText(String.valueOf(n != null ? n : 0)));

        viewModel.nbSessions.observe(this, n ->
                tvNbSessions.setText(String.valueOf(n != null ? n : 0)));

        viewModel.tauxReussite.observe(this, t -> {
            int pct = (t != null) ? Math.round(t) : 0;
            tvTauxReussite.setText(pct + "%");
        });

        viewModel.getLabelPeriode().observe(this, label ->
                tvLabelPeriode.setText(label));

        viewModel.participationParThematique.observe(this,
                this::mettreAJourThematiques);

        viewModel.topFormations.observe(this,
                this::mettreAJourTopFormations);
    }

    // ── Mise à jour des barres thématiques ────────────────────────

    private void mettreAJourThematiques(List<StatThematique> stats) {
        // Reset systématique d'abord
        tvPctInclusion.setText("0%");     pbInclusion.setProgress(0);
        tvPctEnvironnement.setText("0%"); pbEnvironnement.setProgress(0);
        tvPctEgalite.setText("0%");       pbEgalite.setProgress(0);

        if (stats == null || stats.isEmpty()) return; // OK de partir maintenant

        int maxInscrits = 0;
        for (StatThematique s : stats)
            if (s.getNbInscrits() > maxInscrits) maxInscrits = s.getNbInscrits();

        for (StatThematique s : stats) {
            int pct = maxInscrits > 0 ? (s.getNbInscrits() * 100 / maxInscrits) : 0;
            switch (s.getThematique()) {
                case "Inclusion":
                    tvPctInclusion.setText(pct + "%");
                    pbInclusion.setProgress(pct);
                    break;
                case "Environnement":
                    tvPctEnvironnement.setText(pct + "%");
                    pbEnvironnement.setProgress(pct);
                    break;
                case "Egalite": case "Égalité":
                    tvPctEgalite.setText(pct + "%");
                    pbEgalite.setProgress(pct);
                    break;
            }
        }
    }

    // ── Mise à jour du top formations ─────────────────────────────

    private void mettreAJourTopFormations(List<FormationTop> tops) {
        // Reset systématique d'abord
        tvTop1Titre.setText("—"); tvTop1Nb.setText("0");
        tvTop2Titre.setText("—"); tvTop2Nb.setText("0");

        if (tops == null || tops.isEmpty()) return; // OK de partir maintenant

        if (tops.size() >= 1) {
            tvTop1Titre.setText(tops.get(0).getTitre());
            tvTop1Nb.setText(String.valueOf(tops.get(0).getNbInscrits()));
        }
        if (tops.size() >= 2) {
            tvTop2Titre.setText(tops.get(1).getTitre());
            tvTop2Nb.setText(String.valueOf(tops.get(1).getNbInscrits()));
        }
    }

    // ── Sélecteur de période ──────────────────────────────────────

    private void configurerPeriode() {
        btn7j.setOnClickListener(v -> viewModel.setPeriode7Jours());
        btnMois.setOnClickListener(v -> viewModel.setPeriodeMois());
        btnAnnee.setOnClickListener(v -> viewModel.setPeriodeAnnee());
    }

    // ── Export PDF ────────────────────────────────────────────────

    private void configurerBoutonPdf() {
        findViewById(R.id.btnExporterPdf).setOnClickListener(v -> {

            // On collecte toutes les valeurs actuelles des LiveData
            PdfExportHelper.StatsSnapshot snap = new PdfExportHelper.StatsSnapshot();

            Integer nbF = viewModel.nbFormations.getValue();
            Integer nbB = viewModel.nbBenevolesActifs.getValue();
            Integer nbS = viewModel.nbSessions.getValue();
            Float   taux = viewModel.tauxReussite.getValue();
            String  label = viewModel.getLabelPeriode().getValue();

            snap.nbFormations  = nbF  != null ? nbF  : 0;
            snap.nbBenevoles   = nbB  != null ? nbB  : 0;
            snap.nbSessions    = nbS  != null ? nbS  : 0;
            snap.tauxReussite  = taux != null ? Math.round(taux) : 0;
            snap.labelPeriode  = label != null ? label : "";
            snap.thematiques   = viewModel.participationParThematique.getValue();
            snap.topFormations = viewModel.topFormations.getValue();

            PdfExportHelper.exporterEtPartager(this, snap);
        });


    }

    private void setupPeriodeSelector() {
        TextView btn7Jours = findViewById(R.id.btn7Jours);
        TextView btnCeMois = findViewById(R.id.btnCeMois);
        TextView btnAnnee  = findViewById(R.id.btnAnnee);
        TextView tvLabel   = findViewById(R.id.tvLabelPeriode);

        // Sélection par défaut
        selectPeriode(btnCeMois, tvLabel);
        viewModel.setPeriodeMois();

        btn7Jours.setOnClickListener(v -> {
            selectPeriode(btn7Jours, tvLabel);
            viewModel.setPeriode7Jours();        // ← data
        });
        btnCeMois.setOnClickListener(v -> {
            selectPeriode(btnCeMois, tvLabel);
            viewModel.setPeriodeMois();
        });
        btnAnnee.setOnClickListener(v -> {
            selectPeriode(btnAnnee, tvLabel);
            viewModel.setPeriodeAnnee();
        });
    }

    private void selectPeriode(TextView selected, TextView tvLabel) {
        // Reset l'ancien
        if (selectedPeriod != null) {
            selectedPeriod.setBackground(null);
            selectedPeriod.setTextColor(getColor(R.color.text_secondaire));
            selectedPeriod.setTypeface(null, Typeface.NORMAL);
        }

        // Applique sur le nouveau
        selected.setBackgroundResource(R.drawable.background_periode_choisi);
        selected.setTextColor(getColor(R.color.couleur_navy));
        selected.setTypeface(null, Typeface.BOLD);

        selectedPeriod = selected;
    }

}