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
import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Session;
import com.openminds.app.viewmodel.FormationViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NouvelleFormationActivity extends AppCompatActivity {

    //Champs Informations Générales
    private EditText etTitre;
    private EditText etDescription;
    private EditText etDuree;

    //Thématique ─
    private ChipGroup chipGroupThematique;

    //Sessions
    private LinearLayout layoutSessionsContainer;
    private TextView tvNombreSessions;
    private Button btnAddSession;

    //Boutons
    private ImageButton btnBack;
    private Button btnSave;

    //ViewModel
    private FormationViewModel formationViewModel;

    //Liste des sessions en mémoire
    // On stocke les sessions ICI avant le Save final
    // Car on ne connaît pas encore l'id de la Formation
    private final List<Session> sessionsTemporaires = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_nouvelle_formation);


        lierLesVues();

        formationViewModel = new ViewModelProvider(this)
                .get(FormationViewModel.class);


        configurerBoutonRetour();
        configurerChips();
        configurerBoutonAjoutSession();
        configurerBoutonSave();
    }


    // Relie chaque variable Java à son id dans le XML
    // findViewById cherche l'élément par son android:id

    private void lierLesVues() {
        etTitre                 = findViewById(R.id.etTitre);
        etDescription           = findViewById(R.id.etDescription);
        etDuree                 = findViewById(R.id.etDuree);
        chipGroupThematique     = findViewById(R.id.chipGroupThematique);
        layoutSessionsContainer = findViewById(R.id.layoutSessionsContainer);
        tvNombreSessions        = findViewById(R.id.tvNombreSessions);
        btnAddSession           = findViewById(R.id.btnAddSession);
        btnBack                 = findViewById(R.id.btnBack);
        btnSave                 = findViewById(R.id.btnSave);
    }


    // Bouton flèche retour < ferme l'écran
    // finish() détruit l'Activity et retourne à MainActivity

    private void configurerBoutonRetour() {
        btnBack.setOnClickListener(v -> {
            android.util.Log.d("DEBUG_CLICK", "Le bouton retour a été cliqué !");
            finish();
        });
    }



    // Quand un chip est sélectionné :
    //   1. On vide les sessions existantes
    //   2. On pré-remplit avec 2 sessions suggérées  adaptées à la thématique choisie


    private void configurerChips() {
        chipGroupThematique.setOnCheckedStateChangeListener(
                (group, checkedIds) -> {
                    // checkedIds = liste des chips cochés
                    // Avec singleSelection=true → 0 ou 1 chip coché
                    if (checkedIds.isEmpty()) return;

                    // Vide la liste mémoire et l'affichage
                    sessionsTemporaires.clear();
                    layoutSessionsContainer.removeAllViews();

                    // Pré-remplit avec des sessions selon la thématique
                    preRemplirSessions(getThematiqueSelectionnee());
                }
        );
    }


    // Crée 2 sessions pré-remplies selon la thématique
    // Chaque thématique a ses propres dates et places

    private void preRemplirSessions(String thematique) {
        Session s1 = new Session();
        Session s2 = new Session();

        switch (thematique) {

            case "inclusion":
                // Session 1 : En ligne, 20 places
                s1.setType("en_ligne");
                s1.setDateDebut("15/04/2026 09h00");
                s1.setDateFin("15/04/2026 10h00");
                s1.setLienOnline("https://meet.google.com/inclusion-1");
                s1.setPlacesMax(20);
                // Session 2 : Présentielle, 15 places
                s2.setType("presentielle");
                s2.setDateDebut("20/04/2026 14h00");
                s2.setDateFin("20/04/2026 16h00");
                s2.setLieu("Salle A, Centre civique");
                s2.setPlacesMax(15);
                break;

            case "environnement":
                s1.setType("en_ligne");
                s1.setDateDebut("18/04/2026 10h00");
                s1.setDateFin("18/04/2026 11h30");
                s1.setLienOnline("https://meet.google.com/env-1");
                s1.setPlacesMax(30);
                s2.setType("presentielle");
                s2.setDateDebut("25/04/2026 09h00");
                s2.setDateFin("25/04/2026 11h00");
                s2.setLieu("Parc naturel, Entrée principale");
                s2.setPlacesMax(12);
                break;

            case "egalite":
                s1.setType("en_ligne");
                s1.setDateDebut("22/04/2026 14h00");
                s1.setDateFin("22/04/2026 15h30");
                s1.setLienOnline("https://meet.google.com/egalite-1");
                s1.setPlacesMax(25);
                s2.setType("presentielle");
                s2.setDateDebut("28/04/2026 10h00");
                s2.setDateFin("28/04/2026 12h00");
                s2.setLieu("Bibliothèque municipale");
                s2.setPlacesMax(18);
                break;

            case "tolerance":
                s1.setType("en_ligne");
                s1.setDateDebut("10/04/2026 09h00");
                s1.setDateFin("10/04/2026 10h00");
                s1.setLienOnline("https://meet.google.com/tolerance-1");
                s1.setPlacesMax(35);
                s2.setType("presentielle");
                s2.setDateDebut("17/04/2026 14h00");
                s2.setDateFin("17/04/2026 16h00");
                s2.setLieu("Maison des associations");
                s2.setPlacesMax(20);
                break;
        }

        // Ajoute les 2 sessions à la liste mémoire
        sessionsTemporaires.add(s1);
        sessionsTemporaires.add(s2);

        // Affiche chaque session dans l'UI
        afficherSessionDansListe(s1);
        afficherSessionDansListe(s2);

        // Met à jour le compteur "2 sessions"
        mettreAJourCompteur();
    }

    // Bouton "+ Ajouter une session"
    // Ouvre un AlertDialog avec le formulaire de session
    private void configurerBoutonAjoutSession() {
        btnAddSession.setOnClickListener(v -> {

            // Empêche d'ajouter une session sans thématique
            if (getThematiqueSelectionnee().isEmpty()) {
                Toast.makeText(this,
                        "Sélectionnez d'abord une thématique",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Charge la vue du dialogue depuis le XML
            View dialogView = getLayoutInflater()
                    .inflate(R.layout.dialog_ajouter_session, null);


            RadioGroup rgType     = dialogView.findViewById(R.id.rgTypeSession);
            EditText etDateDebut  = dialogView.findViewById(R.id.etDateDebut);
            EditText etDateFin    = dialogView.findViewById(R.id.etDateFin);
            TextView tvLieuLabel  = dialogView.findViewById(R.id.tvLieuLabel);
            EditText etLieu       = dialogView.findViewById(R.id.etLieu);
            TextView tvLienLabel  = dialogView.findViewById(R.id.tvLienLabel);
            EditText etLienOnline = dialogView.findViewById(R.id.etLienOnline);
            EditText etPlacesMax  = dialogView.findViewById(R.id.etPlacesMax);

            // Quand on change le type → affiche/cache les bons champs
            // View.VISIBLE = visible | View.GONE = invisible + sans espace
            rgType.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbPresentielle) {
                    tvLieuLabel.setVisibility(View.VISIBLE);
                    etLieu.setVisibility(View.VISIBLE);
                    tvLienLabel.setVisibility(View.GONE);
                    etLienOnline.setVisibility(View.GONE);
                } else {
                    tvLieuLabel.setVisibility(View.GONE);
                    etLieu.setVisibility(View.GONE);
                    tvLienLabel.setVisibility(View.VISIBLE);
                    etLienOnline.setVisibility(View.VISIBLE);
                }
            });

            // Construit et affiche le dialogue
            new AlertDialog.Builder(this)
                    .setTitle("Ajouter une session")
                    .setView(dialogView)
                    .setPositiveButton("Ajouter", (dialog, which) -> {

                        // Lit le type choisi
                        boolean enLigne =
                                rgType.getCheckedRadioButtonId() == R.id.rbEnLigne;
                        String type = enLigne ? "en_ligne" : "presentielle";

                        String dateDebut = etDateDebut.getText().toString().trim();
                        String dateFin   = etDateFin.getText().toString().trim();
                        String placesStr = etPlacesMax.getText().toString().trim();

                        // Validation — champs obligatoires
                        if (dateDebut.isEmpty() || placesStr.isEmpty()) {
                            Toast.makeText(this,
                                    "Date et places sont obligatoires",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Crée la session avec les SETTERS
                        Session session = new Session();
                        session.setType(type);
                        session.setDateDebut(dateDebut);
                        session.setDateFin(dateFin);
                        session.setPlacesMax(Integer.parseInt(placesStr));

                        if (type.equals("presentielle")) {
                            session.setLieu(
                                    etLieu.getText().toString().trim());
                        } else {
                            session.setLienOnline(
                                    etLienOnline.getText().toString().trim());
                        }

                        // Ajoute à la liste mémoire et affiche dans l'UI
                        sessionsTemporaires.add(session);
                        afficherSessionDansListe(session);
                        mettreAJourCompteur();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }


    private void afficherSessionDansListe(Session session) {
        View vue = getLayoutInflater().inflate(
                R.layout.item_session_preview,
                layoutSessionsContainer,
                false
        );


        TextView tvType          = vue.findViewById(R.id.tvSessionType);
        TextView tvDate          = vue.findViewById(R.id.tvSessionDate);
        TextView tvPlaces        = vue.findViewById(R.id.tvSessionPlaces);
        ImageButton btnSupprimer = vue.findViewById(R.id.btnSupprimerSession);


        String typeLabel = session.getType().equals("en_ligne")
                ? "💻 En Ligne" : "📍 Présentielle";

        tvType.setText(typeLabel);
        tvDate.setText(session.getDateDebut() + " → " + session.getDateFin());
        tvPlaces.setText(session.getPlacesMax() + " places");

        //  Bouton ✕ — supprime cette session
        btnSupprimer.setOnClickListener(v -> {

            // Demande confirmation avant de supprimer
            new AlertDialog.Builder(this)
                    .setTitle("Supprimer la session ?")
                    .setMessage(typeLabel + "\n" + session.getDateDebut())
                    .setPositiveButton("Supprimer", (dialog, which) -> {
                        // 1. Retire de la liste mémoire
                        sessionsTemporaires.remove(session);
                        // 2. Retire la vue de l'écran
                        layoutSessionsContainer.removeView(vue);
                        // 3. Met à jour le compteur
                        mettreAJourCompteur();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        // Attache la vue au container
        layoutSessionsContainer.addView(vue);
    }


    private void mettreAJourCompteur() {
        int nb = sessionsTemporaires.size();
        tvNombreSessions.setText(
                nb + (nb > 1 ? " sessions" : " session"));
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


    // Bouton ENREGISTRER — sauvegarde TOUT en base :

    private void configurerBoutonSave() {
        btnSave.setOnClickListener(v -> {

            String titre      = etTitre.getText().toString().trim();
            String desc       = etDescription.getText().toString().trim();
            String dureeStr   = etDuree.getText().toString().trim();
            String thematique = getThematiqueSelectionnee();

            // ── Validations ───────────────────────────────
            if (titre.isEmpty()) {
                etTitre.setError("Le titre est obligatoire");
                etTitre.requestFocus();
                return;
            }
            if (dureeStr.isEmpty()) {
                etDuree.setError("La durée est obligatoire");
                etDuree.requestFocus();
                return;
            }
            if (thematique.isEmpty()) {
                Toast.makeText(this,
                        "Veuillez sélectionner une thématique",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Créer la Formation avec les SETTERS
            Formation formation = new Formation();
            formation.setTitre(titre);
            formation.setDescription(desc);
            formation.setDureeMinutes(Integer.parseInt(dureeStr));
            formation.setThematique(thematique);
            formation.setDateCreation(
                    new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
                            .format(new Date())
            );

            // Sauvegarder via ViewModel
            formationViewModel.ajouterFormationAvecSessions(
                    formation,
                    sessionsTemporaires,
                    // Callback = code exécuté APRÈS l'insertion en BD
                    formationId -> runOnUiThread(() -> {

                        android.util.Log.d("US15_TEST",
                                "✅ Formation insérée avec id = " + formationId);
                        android.util.Log.d("US15_TEST",
                                "✅ Nombre de sessions = " + sessionsTemporaires.size());


                        Toast.makeText(this,
                                "Formation \"" + titre + "\" enregistrée"
                                        + " avec " + sessionsTemporaires.size()
                                        + " session(s) !",
                                Toast.LENGTH_LONG).show();
                        finish();
                    })
            );
        });
    }
}