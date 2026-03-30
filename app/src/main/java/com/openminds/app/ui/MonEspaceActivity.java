package com.openminds.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.openminds.openminds.R;
import com.openminds.app.viewmodel.UtilisateurViewModel;

public class MonEspaceActivity extends AppCompatActivity {

    private TextView tvNomPrenom, tvAvatar;
    private ImageView btnRetour, btnParametres;
    private UtilisateurViewModel utilisateurViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_espace);

        // 1. Lier les éléments du design XML au code Java
        tvNomPrenom = findViewById(R.id.tvNomPrenom);
        tvAvatar = findViewById(R.id.tvAvatar);
        btnRetour = findViewById(R.id.btnRetour);
        btnParametres = findViewById(R.id.btnParametres);

        // 2. Initialiser la connexion à la base de données
        utilisateurViewModel = new ViewModelProvider(this).get(UtilisateurViewModel.class);

        // 3. Vérifier qui est connecté
        SharedPreferences prefs = getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("connected_user_id", -1);

        if (userId == -1) {
            // Sécurité : Retour à la connexion si aucun utilisateur n'est trouvé
            startActivity(new Intent(this, ConnexionActivity.class));
            finish();
            return;
        }

        // 4. Charger et afficher les vraies données de l'utilisateur
        utilisateurViewModel.getUtilisateurById(userId).observe(this, utilisateur -> {
            if (utilisateur != null) {
                // A. Afficher le Nom et Prénom (ex: "Marie Bénévole")
                String nomComplet = utilisateur.getPrenom() + " " + utilisateur.getNom();
                tvNomPrenom.setText(nomComplet);

                // B. Générer les initiales pour le cercle d'Avatar (ex: "MB")
                String initiales = "";
                if (utilisateur.getPrenom() != null && !utilisateur.getPrenom().isEmpty()) {
                    initiales += utilisateur.getPrenom().substring(0, 1).toUpperCase();
                }
                if (utilisateur.getNom() != null && !utilisateur.getNom().isEmpty()) {
                    initiales += utilisateur.getNom().substring(0, 1).toUpperCase();
                }
                tvAvatar.setText(initiales);
            }
        });

        // 5. Gérer les clics sur les boutons
        btnRetour.setOnClickListener(v -> finish()); // Retourne à l'écran précédent

        btnParametres.setOnClickListener(v -> {
            // Comme il n'y a plus de bouton "Déconnexion" visible, on peut la mettre ici !
            // Ou ouvrir une future activité "ParametresActivity"
            deconnexion(prefs);
        });
    }

    // Méthode pour se déconnecter
    private void deconnexion(SharedPreferences prefs) {
        // Efface la session
        prefs.edit().clear().apply();
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();

        // Retourne à l'écran de connexion et ferme toutes les autres pages
        Intent intent = new Intent(this, ConnexionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}