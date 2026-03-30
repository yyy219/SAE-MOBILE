package com.openminds.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.app.database.entity.Session;
import com.openminds.app.viewmodel.InscriptionViewModel;
import com.openminds.openminds.R;

import java.util.HashMap;
import java.util.Map;

public class ChoisirSessionActivity extends AppCompatActivity {

    public static final String EXTRA_FORMATION_ID    = "formation_id";
    public static final String EXTRA_FORMATION_TITRE = "formation_titre";
    public static final String EXTRA_UTILISATEUR_ID  = "utilisateur_id";

    private InscriptionViewModel viewModel;
    private SessionAdapter adapter;
    private Button btnSinscrire;
    private int utilisateurId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choisir_session);

        Intent intent = getIntent();
        int formationId = intent.getIntExtra(EXTRA_FORMATION_ID, -1);
        String titreFormation = intent.getStringExtra(EXTRA_FORMATION_TITRE);
        utilisateurId = intent.getIntExtra(EXTRA_UTILISATEUR_ID, 1);

        TextView tvTitreFormation = findViewById(R.id.tvTitreFormation);
        RecyclerView recyclerSessions = findViewById(R.id.recyclerSessions);
        btnSinscrire = findViewById(R.id.btnSinscrire);
        TextView tvAnnuler = findViewById(R.id.tvAnnuler);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        if (titreFormation != null) {
            tvTitreFormation.setText(titreFormation);
        }

        viewModel = new ViewModelProvider(this).get(InscriptionViewModel.class);

        adapter = new SessionAdapter(this, (session, estComplet) -> {
            btnSinscrire.setEnabled(true);
            btnSinscrire.setAlpha(1.0f);
        });
        recyclerSessions.setLayoutManager(new LinearLayoutManager(this));
        recyclerSessions.setAdapter(adapter);

        if (formationId != -1) {
            viewModel.getSessionsByFormation(formationId).observe(this, sessions -> {
                if (sessions != null) {
                    adapter.setInscritsMap(new HashMap<>());
                    adapter.setSessions(sessions);
                }
            });
        }

        viewModel.getMessageInscription().observe(this, message -> {
            if (message != null && message.equals("Inscription réussie !")) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                finish();
            } else if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        btnSinscrire.setEnabled(false);
        btnSinscrire.setAlpha(0.5f);
        btnSinscrire.setOnClickListener(v -> {
            Session sessionSelectionnee = adapter.getSelectedSession();
            if (sessionSelectionnee != null) {
                afficherDialogInscription(sessionSelectionnee);
            } else {
                Toast.makeText(this, "Veuillez sélectionner une session.", Toast.LENGTH_SHORT).show();
            }
        });

        tvAnnuler.setOnClickListener(v -> finish());
    }

    private void afficherDialogInscription(Session session) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_inscription, null);

        EditText etNom      = dialogView.findViewById(R.id.etNom);
        EditText etPrenom   = dialogView.findViewById(R.id.etPrenom);
        EditText etEmail    = dialogView.findViewById(R.id.etEmail);
        EditText etTelephone = dialogView.findViewById(R.id.etTelephone);

        new AlertDialog.Builder(this)
                .setTitle("Confirmer l'inscription")
                .setView(dialogView)
                .setPositiveButton("S'inscrire", (dialog, which) -> {
                    String nom       = etNom.getText().toString().trim();
                    String prenom    = etPrenom.getText().toString().trim();
                    String email     = etEmail.getText().toString().trim();
                    String telephone = etTelephone.getText().toString().trim();

                    if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                        Toast.makeText(this, "Veuillez remplir tous les champs obligatoires.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Inscription en BD
                    viewModel.inscrire(utilisateurId, session.getId());
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}