package com.openminds.app.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.openminds.openminds.R;
import com.openminds.app.database.entity.Utilisateur;
import com.openminds.app.viewmodel.UtilisateurViewModel;

public class InscriptionActivity extends AppCompatActivity {

    private EditText etNom, etPrenom, etEmail, etPassword;
    private Button btnInscrire;
    private UtilisateurViewModel utilisateurViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        // Tab "Connexion" en haut → retour à l'écran de connexion
        TextView tabConnexion = findViewById(R.id.tabConnexion);
        if (tabConnexion != null) {
            tabConnexion.setOnClickListener(v -> finish());
        }
        // Lien "Se connecter" en bas → idem
        TextView tvAllerConnexion = findViewById(R.id.tvAllerConnexion);
        if (tvAllerConnexion != null) {
            tvAllerConnexion.setOnClickListener(v -> finish());
        }

        etNom    = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        // FIX #3 : les vrais IDs dans activity_inscription.xml
        etEmail    = findViewById(R.id.etEmailInscription);
        etPassword = findViewById(R.id.etPasswordInscription);
        // FIX #4 : le vrai ID du bouton dans activity_inscription.xml
        btnInscrire = findViewById(R.id.btnCreerCompte);

        utilisateurViewModel = new ViewModelProvider(this).get(UtilisateurViewModel.class);

        // Observer l'email déjà utilisé pour afficher un message adapté
        utilisateurViewModel.getEmailDejaUtilise().observe(this, dejaUtilise -> {
            if (dejaUtilise != null) {
                if (dejaUtilise) {
                    Toast.makeText(this, "Cet email est déjà utilisé.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Inscription réussie, connectez-vous !", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btnInscrire.setOnClickListener(v -> {
            String nom     = etNom.getText().toString().trim();
            String prenom  = etPrenom.getText().toString().trim();
            String email   = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            Utilisateur nouvelUtilisateur = new Utilisateur();
            nouvelUtilisateur.setNom(nom);
            nouvelUtilisateur.setPrenom(prenom);
            nouvelUtilisateur.setEmail(email);
            nouvelUtilisateur.setMotDePasse(password);
            nouvelUtilisateur.setRole("benevole");

            // FIX #5 : insert() n'existe pas dans le ViewModel → utiliser creerCompte()
            utilisateurViewModel.creerCompte(nouvelUtilisateur);
        });
    }
}