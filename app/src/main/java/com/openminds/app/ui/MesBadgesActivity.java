    package com.openminds.app.ui;

    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.lifecycle.ViewModelProvider;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.openminds.app.viewmodel.StatistiquesViewModel;
    import com.openminds.openminds.R; // Modifie si ton R est dans com.openminds.openminds
    import com.openminds.app.database.entity.Formation;
    import com.openminds.app.database.entity.Inscription;
    import com.openminds.app.viewmodel.FormationViewModel;
    import com.openminds.app.viewmodel.UtilisateurViewModel;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;

    public class MesBadgesActivity extends AppCompatActivity {

        private String nomCompletUtilisateur = "Utilisateur";
        private BadgeAdapter adapter;
        private TextView tvEmptyBadges;
        private RecyclerView rvBadges;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mes_badges);

            rvBadges = findViewById(R.id.rvBadges);
            tvEmptyBadges = findViewById(R.id.tvEmptyBadges);
            rvBadges.setLayoutManager(new LinearLayoutManager(this));


            adapter = new BadgeAdapter(formation -> {
                String dateDuJour = new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE).format(new Date());
                PdfExportHelper.exportAttestationPdf(this, nomCompletUtilisateur, formation.getTitre(), dateDuJour);
            });
            rvBadges.setAdapter(adapter);


            SharedPreferences prefs = getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("connected_user_id", -1);

            if (userId == -1) {
                startActivity(new Intent(this, ConnexionActivity.class));
                finish();
                return;
            }

            UtilisateurViewModel userVM = new ViewModelProvider(this).get(UtilisateurViewModel.class);
            StatistiquesViewModel statsVM = new ViewModelProvider(this).get(StatistiquesViewModel.class);


            userVM.getUtilisateurById(userId).observe(this, u -> {
                if (u != null) {
                    nomCompletUtilisateur = u.getPrenom() + " " + u.getNom();
                }
            });


            statsVM.getFormationsTerminees(userId).observe(this, badgesObtenus -> {
                if (badgesObtenus != null) {


                    adapter.setFormations(badgesObtenus);


                    if (badgesObtenus.isEmpty()) {
                        tvEmptyBadges.setVisibility(View.VISIBLE);
                        rvBadges.setVisibility(View.GONE);
                    } else {
                        tvEmptyBadges.setVisibility(View.GONE);
                        rvBadges.setVisibility(View.VISIBLE);
                    }
                }
            });

            setupBottomNavigation();
        }

        private void setupBottomNavigation() {
            View navCatalogue = findViewById(R.id.navCatalogue);
            View navMonEspace = findViewById(R.id.navMonEspace);

            if (navCatalogue != null) navCatalogue.setOnClickListener(v -> { startActivity(new Intent(this, CatalogueFormationsActivity.class)); finish(); });
            if (navMonEspace != null) navMonEspace.setOnClickListener(v -> { startActivity(new Intent(this, MonEspaceActivity.class)); finish(); });
        }
    }