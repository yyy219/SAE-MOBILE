package com.openminds.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.openminds.app.viewmodel.ContenuViewModel;
import com.openminds.openminds.R;

public class ModuleDetailActivity extends AppCompatActivity {

    private ContenuViewModel contenuViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_detail); // Assure-toi d'avoir gardé ton XML !

        TextView tvTitre = findViewById(R.id.tv_detail_titre);
        TextView tvTexteCours = findViewById(R.id.tv_detail_texte);
        Button btnAction = findViewById(R.id.btn_detail_action);

        LinearLayout sectionTexte = findViewById(R.id.section_texte);
        LinearLayout sectionVideo = findViewById(R.id.section_video);
        LinearLayout sectionQuiz = findViewById(R.id.section_quiz);

        int contenuId = getIntent().getIntExtra("CONTENU_ID", -1);

        if (contenuId == -1) {
            Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // On passe par le ViewModel comme l'exige la documentation v2
        contenuViewModel = new ViewModelProvider(this).get(ContenuViewModel.class);

        contenuViewModel.getContenuById(contenuId).observe(this, module -> {
            if (module != null) {
                // Le titre reste le même pour tout le monde
                tvTitre.setText(module.getTitre());

                // On cache TOUTES les sections par précaution
                sectionTexte.setVisibility(android.view.View.GONE);
                sectionVideo.setVisibility(android.view.View.GONE);
                sectionQuiz.setVisibility(android.view.View.GONE);

                // On affiche la bonne section selon le TYPE
                if ("video".equals(module.getType())) {
                    sectionVideo.setVisibility(android.view.View.VISIBLE);
                    btnAction.setText("J'ai terminé la vidéo");

                } else if ("quiz".equals(module.getType())) {
                    sectionQuiz.setVisibility(android.view.View.VISIBLE);
                    btnAction.setText("Valider mes réponses");

                } else {
                    // Par défaut, c'est du texte
                    sectionTexte.setVisibility(android.view.View.VISIBLE);
                    tvTexteCours.setText(module.getContenuTexte());
                    btnAction.setText("J'ai lu ce module");
                }
            }
        });

        // Bouton pour fermer et renvoyer la validation à la liste
        btnAction.setOnClickListener(v -> {
            // On prépare un message (Intent) de retour
            Intent resultIntent = new Intent();
            resultIntent.putExtra("MODULE_VALIDE_ID", contenuId); // On attache l'ID validé

            // On indique que tout s'est bien passé (RESULT_OK) et on y joint le message
            setResult(RESULT_OK, resultIntent);

            // On ferme la page
            finish();
        });
    }
}
