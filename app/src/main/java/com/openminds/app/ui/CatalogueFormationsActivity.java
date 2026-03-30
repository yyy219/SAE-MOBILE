package com.openminds.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.app.viewmodel.FormationViewModel;
import com.openminds.openminds.R;

public class CatalogueFormationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue_formations);

        ImageButton btnRetour = findViewById(R.id.btnBack);
        btnRetour.setOnClickListener(v -> finish());

        RecyclerView recycler = findViewById(R.id.recyclerFormations);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        FormationViewModel viewModel = new ViewModelProvider(this).get(FormationViewModel.class);

        CatalogueAdapter adapter = new CatalogueAdapter(formation -> {
            // Clic sur une formation → ouvre ChoisirSessionActivity
            Intent intent = new Intent(this, ChoisirSessionActivity.class);
            intent.putExtra(ChoisirSessionActivity.EXTRA_FORMATION_ID, formation.getId());
            intent.putExtra(ChoisirSessionActivity.EXTRA_FORMATION_TITRE, formation.getTitre());
            intent.putExtra(ChoisirSessionActivity.EXTRA_UTILISATEUR_ID, 1); // à remplacer par l'utilisateur connecté
            startActivity(intent);
        });

        recycler.setAdapter(adapter);

        viewModel.toutesLesFormations.observe(this, formations -> {
            if (formations != null) {
                adapter.setFormations(formations);
            }
        });
    }
}
