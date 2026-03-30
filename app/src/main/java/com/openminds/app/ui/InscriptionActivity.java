package com.openminds.app.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
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

    private EditText etNom, etPrenom, etEmail, etPassword, etConfirm;
    private Button btnInscrire;
    private View seg1, seg2, seg3, seg4;
    private UtilisateurViewModel utilisateurViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        // Tab Connexion → retour
        TextView tabConnexion = findViewById(R.id.tabConnexion);
        if (tabConnexion != null) tabConnexion.setOnClickListener(v -> finish());

        TextView tvAllerConnexion = findViewById(R.id.tvAllerConnexion);
        if (tvAllerConnexion != null) tvAllerConnexion.setOnClickListener(v -> finish());

        // Bind views
        etNom       = findViewById(R.id.etNom);
        etPrenom    = findViewById(R.id.etPrenom);
        etEmail     = findViewById(R.id.etEmailInscription);
        etPassword  = findViewById(R.id.etPasswordInscription);
        etConfirm   = findViewById(R.id.etConfirmPassword);
        btnInscrire = findViewById(R.id.btnCreerCompte);
        seg1 = findViewById(R.id.seg1);
        seg2 = findViewById(R.id.seg2);
        seg3 = findViewById(R.id.seg3);
        seg4 = findViewById(R.id.seg4);

        utilisateurViewModel = new ViewModelProvider(this).get(UtilisateurViewModel.class);

        // Barre de force du mot de passe
        if (etPassword != null) {
            etPassword.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    mettreAJourBarreForce(s.toString());
                }
            });
        }

        // Observer résultat inscription
        utilisateurViewModel.getEmailDejaUtilise().observe(this, dejaUtilise -> {
            if (dejaUtilise == null) return;
            if (dejaUtilise) {
                if (etEmail != null) etEmail.setError("Cet email est déjà utilisé");
                Toast.makeText(this, "Cet email est déjà utilisé", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Compte créé ! Connectez-vous", Toast.LENGTH_SHORT).show();
                // Retour à la connexion avec email prérempli
                Intent intent = new Intent(this, ConnexionActivity.class);
                String email = etEmail != null ? etEmail.getText().toString().trim() : "";
                intent.putExtra("email_prefill", email);
                startActivity(intent);
                finish();
            }
        });

        // Bouton créer compte
        btnInscrire.setOnClickListener(v -> {
            String nom      = etNom.getText().toString().trim();
            String prenom   = etPrenom.getText().toString().trim();
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm  = etConfirm != null ? etConfirm.getText().toString().trim() : password;

            // Champs vides
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Format email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Email invalide");
                etEmail.requestFocus();
                return;
            }

            // Longueur mdp
            if (password.length() < 6) {
                etPassword.setError("Minimum 6 caractères");
                etPassword.requestFocus();
                return;
            }

            // Confirmation mdp
            if (!password.equals(confirm)) {
                if (etConfirm != null) etConfirm.setError("Les mots de passe ne correspondent pas");
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            Utilisateur u = new Utilisateur();
            u.setNom(nom);
            u.setPrenom(prenom);
            u.setEmail(email);
            u.setMotDePasse(password);
            u.setRole("benevole");
            u.setDateInscription(new java.util.Date().toString());

            utilisateurViewModel.creerCompte(u);
        });
    }

    // Colore les segments selon la force du mot de passe
    private void mettreAJourBarreForce(String mdp) {
        View[] segs = {seg1, seg2, seg3, seg4};

        // Si vide → tout gris
        if (mdp.isEmpty()) {
            for (View s : segs)
                if (s != null) s.setBackgroundColor(Color.parseColor("#E5E7EB"));
            return;
        }

        // Calcul score 1-4
        int score = 0;
        if (mdp.length() >= 4)  score++;
        if (mdp.length() >= 8)  score++;
        if (mdp.matches(".*[A-Z].*") && mdp.matches(".*[0-9].*")) score++;
        if (mdp.matches(".*[!@#$%^&*()_+=].*")) score++;
        if (score == 0) score = 1; // au moins 1 segment rouge si quelque chose est tapé

        // Couleur selon score
        int couleurActive;
        if (score == 1)      couleurActive = Color.parseColor("#EF4444"); // rouge
        else if (score == 2) couleurActive = Color.parseColor("#F97316"); // orange
        else if (score == 3) couleurActive = Color.parseColor("#EAB308"); // jaune
        else                 couleurActive = Color.parseColor("#1ECFB8"); // turquoise

        int couleurInactive = Color.parseColor("#E5E7EB");

        for (int i = 0; i < segs.length; i++) {
            if (segs[i] != null)
                segs[i].setBackgroundColor(i < score ? couleurActive : couleurInactive);
        }
    }
}