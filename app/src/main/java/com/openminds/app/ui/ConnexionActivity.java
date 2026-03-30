package com.openminds.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.openminds.openminds.R;
import com.openminds.app.viewmodel.UtilisateurViewModel;

public class ConnexionActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnConnexion;
    private TextView tvInscription;
    private ProgressBar progressBar;
    private UtilisateurViewModel utilisateurViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_connexion);

        etEmail      = findViewById(R.id.etEmail);
        etPassword   = findViewById(R.id.etPassword);
        btnConnexion = findViewById(R.id.btnConnexion);
        tvInscription = findViewById(R.id.tvAllerInscription);
        progressBar  = findViewById(R.id.progressBarConnexion);

        // Tab Inscription en haut
        TextView tabInscription = findViewById(R.id.tabInscription);
        if (tabInscription != null) {
            tabInscription.setOnClickListener(v ->
                    startActivity(new Intent(this, InscriptionActivity.class)));
        }

        utilisateurViewModel = new ViewModelProvider(this).get(UtilisateurViewModel.class);

        // Observer connexion réussie
        utilisateurViewModel.getUtilisateurConnecte().observe(this, utilisateur -> {
            if (utilisateur != null) {
                prefs.edit().putInt("connected_user_id", utilisateur.getId()).apply();
                setLoading(false);
                Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MonEspaceActivity.class));
                finish();
            }
        });

        // Observer échec connexion
        utilisateurViewModel.getLoginResultat().observe(this, succes -> {
            if (succes != null && !succes) {
                setLoading(false);
                Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton connexion
        btnConnexion.setOnClickListener(v -> {
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Vérif champs vides
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérif format email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Email invalide");
                etEmail.requestFocus();
                return;
            }

            setLoading(true);
            utilisateurViewModel.login(email, password);
        });

        // Lien S'inscrire en bas
        tvInscription.setOnClickListener(v ->
                startActivity(new Intent(this, InscriptionActivity.class)));
    }

    private void setLoading(boolean loading) {
        if (progressBar != null)
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnConnexion.setEnabled(!loading);
        btnConnexion.setAlpha(loading ? 0.6f : 1f);
    }
}