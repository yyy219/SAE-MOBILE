package com.openminds.app.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.ChipGroup;
import com.openminds.openminds.R;
import com.openminds.app.database.entity.Contenu;
import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Session;
import com.openminds.app.viewmodel.FormationViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NouvelleFormationActivity extends AppCompatActivity {

    private EditText etTitre, etDescription, etDuree;
    private ChipGroup chipGroupThematique;
    private LinearLayout layoutSessionsContainer, layoutModulesContainer;
    private TextView tvNombreSessions, tvNombreModules;
    private Button btnAddSession, btnAddModule;
    private ImageButton btnBack;
    private Button btnSave;
    private FormationViewModel formationViewModel;
    private final List<Session> sessionsTemporaires = new ArrayList<>();
    private final List<Contenu> modulesTemporaires = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nouvelle_formation);
        lierLesVues();
        formationViewModel = new ViewModelProvider(this).get(FormationViewModel.class);
        configurerBoutonRetour();
        configurerChips();
        configurerBoutonAjoutSession();
        configurerBoutonAjoutModule();
        configurerBoutonSave();
    }

    private void lierLesVues() {
        etTitre                 = findViewById(R.id.etTitre);
        etDescription           = findViewById(R.id.etDescription);
        etDuree                 = findViewById(R.id.etDuree);
        chipGroupThematique     = findViewById(R.id.chipGroupThematique);
        layoutSessionsContainer = findViewById(R.id.layoutSessionsContainer);
        layoutModulesContainer  = findViewById(R.id.layoutModulesContainer);
        tvNombreSessions        = findViewById(R.id.tvNombreSessions);
        tvNombreModules         = findViewById(R.id.tvNombreModules);
        btnAddSession           = findViewById(R.id.btnAddSession);
        btnAddModule            = findViewById(R.id.btnAddModule);
        btnBack                 = findViewById(R.id.btnRetour);
        btnSave                 = findViewById(R.id.btnSave);
    }

    private void configurerBoutonRetour() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void configurerChips() {
        chipGroupThematique.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            sessionsTemporaires.clear();
            layoutSessionsContainer.removeAllViews();
            preRemplirSessions(getThematiqueSelectionnee());
        });
    }

    private void preRemplirSessions(String thematique) {
        Session s1 = new Session();
        Session s2 = new Session();
        switch (thematique) {
            case "inclusion":
                s1.setType("en_ligne"); s1.setDateDebut("15/04/2026 09h00"); s1.setDateFin("15/04/2026 10h00"); s1.setLienOnline("https://meet.google.com/inclusion-1"); s1.setPlacesMax(20);
                s2.setType("presentielle"); s2.setDateDebut("20/04/2026 14h00"); s2.setDateFin("20/04/2026 16h00"); s2.setLieu("Salle A, Centre civique"); s2.setPlacesMax(15);
                break;
            case "environnement":
                s1.setType("en_ligne"); s1.setDateDebut("18/04/2026 10h00"); s1.setDateFin("18/04/2026 11h30"); s1.setLienOnline("https://meet.google.com/env-1"); s1.setPlacesMax(30);
                s2.setType("presentielle"); s2.setDateDebut("25/04/2026 09h00"); s2.setDateFin("25/04/2026 11h00"); s2.setLieu("Parc naturel, Entrée principale"); s2.setPlacesMax(12);
                break;
            case "egalite":
                s1.setType("en_ligne"); s1.setDateDebut("22/04/2026 14h00"); s1.setDateFin("22/04/2026 15h30"); s1.setLienOnline("https://meet.google.com/egalite-1"); s1.setPlacesMax(25);
                s2.setType("presentielle"); s2.setDateDebut("28/04/2026 10h00"); s2.setDateFin("28/04/2026 12h00"); s2.setLieu("Bibliothèque municipale"); s2.setPlacesMax(18);
                break;
            case "tolerance":
                s1.setType("en_ligne"); s1.setDateDebut("10/04/2026 09h00"); s1.setDateFin("10/04/2026 10h00"); s1.setLienOnline("https://meet.google.com/tolerance-1"); s1.setPlacesMax(35);
                s2.setType("presentielle"); s2.setDateDebut("17/04/2026 14h00"); s2.setDateFin("17/04/2026 16h00"); s2.setLieu("Maison des associations"); s2.setPlacesMax(20);
                break;
        }
        sessionsTemporaires.add(s1); sessionsTemporaires.add(s2);
        afficherSessionDansListe(s1); afficherSessionDansListe(s2);
        mettreAJourCompteurSessions();
    }

    private void configurerBoutonAjoutSession() {
        btnAddSession.setOnClickListener(v -> {
            if (getThematiqueSelectionnee().isEmpty()) {
                Toast.makeText(this, "Sélectionnez d'abord une thématique", Toast.LENGTH_SHORT).show();
                return;
            }
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_ajouter_session, null);
            RadioGroup rgType     = dialogView.findViewById(R.id.rgTypeSession);
            EditText etDateDebut  = dialogView.findViewById(R.id.etDateDebut);
            EditText etDateFin    = dialogView.findViewById(R.id.etDateFin);
            TextView tvLieuLabel  = dialogView.findViewById(R.id.tvLieuLabel);
            EditText etLieu       = dialogView.findViewById(R.id.etLieu);
            TextView tvLienLabel  = dialogView.findViewById(R.id.tvLienLabel);
            EditText etLienOnline = dialogView.findViewById(R.id.etLienOnline);
            EditText etPlacesMax  = dialogView.findViewById(R.id.etPlacesMax);

            rgType.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbPresentielle) {
                    tvLieuLabel.setVisibility(View.VISIBLE); etLieu.setVisibility(View.VISIBLE);
                    tvLienLabel.setVisibility(View.GONE); etLienOnline.setVisibility(View.GONE);
                } else {
                    tvLieuLabel.setVisibility(View.GONE); etLieu.setVisibility(View.GONE);
                    tvLienLabel.setVisibility(View.VISIBLE); etLienOnline.setVisibility(View.VISIBLE);
                }
            });

            new AlertDialog.Builder(this)
                    .setTitle("Ajouter une session")
                    .setView(dialogView)
                    .setPositiveButton("Ajouter", (dialog, which) -> {
                        boolean enLigne = rgType.getCheckedRadioButtonId() == R.id.rbEnLigne;
                        String type = enLigne ? "en_ligne" : "presentielle";
                        String dateDebut = etDateDebut.getText().toString().trim();
                        String placesStr = etPlacesMax.getText().toString().trim();
                        if (dateDebut.isEmpty() || placesStr.isEmpty()) {
                            Toast.makeText(this, "Date et places sont obligatoires", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Session session = new Session();
                        session.setType(type);
                        session.setDateDebut(dateDebut);
                        session.setDateFin(etDateFin.getText().toString().trim());
                        session.setPlacesMax(Integer.parseInt(placesStr));
                        if (type.equals("presentielle")) session.setLieu(etLieu.getText().toString().trim());
                        else session.setLienOnline(etLienOnline.getText().toString().trim());
                        sessionsTemporaires.add(session);
                        afficherSessionDansListe(session);
                        mettreAJourCompteurSessions();
                    })
                    .setNegativeButton("Annuler", null).show();
        });
    }

    private void configurerBoutonAjoutModule() {
        btnAddModule.setOnClickListener(v -> {
            // Création du champ pour le titre
            EditText etTitreModule = new EditText(this);
            etTitreModule.setHint("Titre du module");

            // Création du titre pour les options
            TextView tvTypeLabel = new TextView(this);
            tvTypeLabel.setText("Type de contenu :");
            tvTypeLabel.setPadding(0, 32, 0, 16);
            tvTypeLabel.setTypeface(null, android.graphics.Typeface.BOLD);

            // Création des choix (Boutons Radio)
            RadioGroup rgType = new RadioGroup(this);

            android.widget.RadioButton rbTexte = new android.widget.RadioButton(this);
            rbTexte.setId(View.generateViewId()); // <-- L'ID unique généré
            rbTexte.setText("Texte (Lecture)");
            rbTexte.setChecked(true); // Sélectionné par défaut

            android.widget.RadioButton rbVideo = new android.widget.RadioButton(this);
            rbVideo.setId(View.generateViewId()); // <-- L'ID unique généré
            rbVideo.setText("Vidéo");

            android.widget.RadioButton rbQuiz = new android.widget.RadioButton(this);
            rbQuiz.setId(View.generateViewId()); // <-- L'ID unique généré
            rbQuiz.setText("Quiz (QCM)");

            rgType.addView(rbTexte);
            rgType.addView(rbVideo);
            rgType.addView(rbQuiz);

            // Assemblage de la vue
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(48, 24, 48, 24);
            layout.addView(etTitreModule);
            layout.addView(tvTypeLabel);
            layout.addView(rgType);

            new AlertDialog.Builder(this)
                    .setTitle("Ajouter un module")
                    .setView(layout)
                    .setPositiveButton("Ajouter", (dialog, which) -> {
                        String titre = etTitreModule.getText().toString().trim();

                        if (titre.isEmpty()) {
                            Toast.makeText(this, "Le titre est obligatoire", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Déterminer le type choisi
                        String typeChoisi = "texte";
                        if (rbVideo.isChecked()) typeChoisi = "video";
                        else if (rbQuiz.isChecked()) typeChoisi = "quiz";

                        Contenu module = new Contenu();
                        module.setTitre(titre);
                        module.setType(typeChoisi);

                        // On met un texte par défaut pour éviter que ce soit vide côté bénévole
                        module.setContenuTexte("Contenu à définir par l'administrateur...");

                        modulesTemporaires.add(module);
                        afficherModuleDansListe(module);
                        mettreAJourCompteurModules();
                    })
                    .setNegativeButton("Annuler", null).show();
        });
    }

    private void afficherSessionDansListe(Session session) {
        View vue = getLayoutInflater().inflate(R.layout.item_session_preview, layoutSessionsContainer, false);
        TextView tvType = vue.findViewById(R.id.tvSessionType);
        TextView tvDate = vue.findViewById(R.id.tvSessionDate);
        TextView tvPlaces = vue.findViewById(R.id.tvSessionPlaces);
        ImageButton btnSupprimer = vue.findViewById(R.id.btnSupprimerSession);
        String typeLabel = session.getType().equals("en_ligne") ? "💻 En Ligne" : "📍 Présentielle";
        tvType.setText(typeLabel);
        tvDate.setText(session.getDateDebut() + " → " + session.getDateFin());
        tvPlaces.setText(session.getPlacesMax() + " places");
        btnSupprimer.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Supprimer la session ?")
                        .setPositiveButton("Supprimer", (d, w) -> {
                            sessionsTemporaires.remove(session);
                            layoutSessionsContainer.removeView(vue);
                            mettreAJourCompteurSessions();
                        })
                        .setNegativeButton("Annuler", null).show()
        );
        layoutSessionsContainer.addView(vue);
    }

    private void afficherModuleDansListe(Contenu module) {
        TextView tv = new TextView(this);

        // Choix de l'icône selon le type
        String icone = "📖 "; // Par défaut texte
        if ("video".equals(module.getType())) icone = "▶️ ";
        else if ("quiz".equals(module.getType())) icone = "❓ ";

        tv.setText(icone + module.getTitre());
        tv.setPadding(16, 16, 16, 16);
        tv.setTextSize(14);
        tv.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Supprimer ce module ?")
                    .setPositiveButton("Supprimer", (d, w) -> {
                        modulesTemporaires.remove(module);
                        layoutModulesContainer.removeView(tv);
                        mettreAJourCompteurModules();
                    })
                    .setNegativeButton("Annuler", null).show();
            return true;
        });
        layoutModulesContainer.addView(tv);
    }

    private void mettreAJourCompteurSessions() {
        int nb = sessionsTemporaires.size();
        tvNombreSessions.setText(nb + (nb > 1 ? " sessions" : " session"));
    }

    private void mettreAJourCompteurModules() {
        int nb = modulesTemporaires.size();
        tvNombreModules.setText(nb + (nb > 1 ? " modules" : " module"));
    }

    private String getThematiqueSelectionnee() {
        int id = chipGroupThematique.getCheckedChipId();
        if (id == -1)                      return "";
        if (id == R.id.chipInclusion)      return "inclusion";
        if (id == R.id.chipEnv)            return "environnement";
        if (id == R.id.chipEgalite)        return "egalite";
        if (id == R.id.chipTolerance)      return "tolerance";
        return "";
    }

    private void configurerBoutonSave() {
        btnSave.setOnClickListener(v -> {
            String titre      = etTitre.getText().toString().trim();
            String desc       = etDescription.getText().toString().trim();
            String dureeStr   = etDuree.getText().toString().trim();
            String thematique = getThematiqueSelectionnee();

            if (titre.isEmpty()) { etTitre.setError("Le titre est obligatoire"); etTitre.requestFocus(); return; }
            if (dureeStr.isEmpty()) { etDuree.setError("La durée est obligatoire"); etDuree.requestFocus(); return; }
            if (thematique.isEmpty()) { Toast.makeText(this, "Veuillez sélectionner une thématique", Toast.LENGTH_SHORT).show(); return; }

            Formation formation = new Formation();
            formation.setTitre(titre);
            formation.setDescription(desc);
            formation.setDureeMinutes(Integer.parseInt(dureeStr));
            formation.setThematique(thematique);
            formation.setDateCreation(new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(new Date()));

            formationViewModel.ajouterFormationAvecSessionsEtModules(
                    formation, sessionsTemporaires, modulesTemporaires,
                    formationId -> runOnUiThread(() -> {
                        Toast.makeText(this, "Formation \"" + titre + "\" enregistrée avec " + sessionsTemporaires.size() + " session(s) et " + modulesTemporaires.size() + " module(s) !", Toast.LENGTH_LONG).show();
                        finish();
                    })
            );
        });
    }
}