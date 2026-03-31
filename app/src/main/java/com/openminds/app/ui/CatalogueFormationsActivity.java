package com.openminds.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.app.viewmodel.FormationViewModel;
import com.openminds.app.viewmodel.TelechargementViewModel;
import com.openminds.openminds.R;

import java.util.HashSet;
import java.util.Set;

public class CatalogueFormationsActivity extends AppCompatActivity {

    private CatalogueAdapter adapter;
    private TelechargementViewModel telechargementViewModel;
    private FormationViewModel formationViewModel;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue_formations);

        currentUserId = getSharedPreferences("OpenMindsPrefs", MODE_PRIVATE)
                .getInt("connected_user_id", -1);

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
        findViewById(R.id.btnMesFormations).setOnClickListener(v ->
                startActivity(new Intent(this, MesformationsActivity.class)));

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFormations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CatalogueAdapter(formation -> {
            Intent intent = new Intent(this, ContenuActivity.class);
            intent.putExtra("formationId", formation.getId());
            startActivity(intent);
        });

        adapter.setTelechargerListener((formation, dejaTelechargee) -> {
            if (dejaTelechargee) {
                new AlertDialog.Builder(this)
                        .setTitle("Supprimer le téléchargement")
                        .setMessage("Retirer \"" + formation.getTitre() + "\" de vos formations hors ligne ?")
                        .setPositiveButton("Supprimer", (d, w) -> {
                            telechargementViewModel.supprimerTelechargement(currentUserId, formation.getId());
                            Toast.makeText(this, "Formation supprimée de l'offline", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
            } else {
                telechargementViewModel.telecharger(currentUserId, formation.getId());
                Toast.makeText(this, "Formation téléchargée ✓", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        formationViewModel = new ViewModelProvider(this).get(FormationViewModel.class);
        telechargementViewModel = new ViewModelProvider(this).get(TelechargementViewModel.class);

        formationViewModel.toutesLesFormations.observe(this, formations -> {
            if (formations != null) adapter.setFormations(formations);
        });

        telechargementViewModel.getFormationIdsTelechargeesLive(currentUserId)
                .observe(this, ids -> {
                    Set<Integer> set = ids != null ? new HashSet<>(ids) : new HashSet<>();
                    adapter.setFormationsTelechargees(set);
                });
    }
}