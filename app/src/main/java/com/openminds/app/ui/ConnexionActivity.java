package com.openminds.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
    private UtilisateurViewModel utilisateurViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérifier si l'utilisateur est déjà connecté
        SharedPreferences prefs = getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE);
        if (prefs.getInt("connected_user_id", -1) != -1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_connexion);

        etEmail      = findViewById(R.id.etEmail);
        etPassword   = findViewById(R.id.etPassword);
        btnConnexion = findViewById(R.id.btnConnexion);
        tvInscription = findViewById(R.id.tvAllerInscription);

        // Tab "Inscription" en haut → switche vers l'écran d'inscription
        TextView tabInscription = findViewById(R.id.tabInscription);
        if (tabInscription != null) {
            tabInscription.setOnClickListener(v ->
                startActivity(new Intent(this, InscriptionActivity.class))
            );
        }

        utilisateurViewModel = new ViewModelProvider(this).get(UtilisateurViewModel.class);

        utilisateurViewModel.getUtilisateurConnecte().observe(this, utilisateur -> {
            if (utilisateur != null) {
                prefs.edit().putInt("connected_user_id", utilisateur.getId()).apply();
                Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                // Redirige vers Mon Espace après connexion
                startActivity(new Intent(this, MonEspaceActivity.class));
                finish();
            }
        });

        utilisateurViewModel.getLoginResultat().observe(this, succes -> {
            if (succes != null && !succes) {
                Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
            }
        });

        btnConnexion.setOnClickListener(v -> {
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            utilisateurViewModel.login(email, password);
        });

        // Lien "S'inscrire" en bas de page → même effet
        tvInscription.setOnClickListener(v ->
                startActivity(new Intent(this, InscriptionActivity.class))
        );
    }
}