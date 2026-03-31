package com.openminds.app.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.app.database.AppDatabase;
import com.openminds.app.database.entity.Contenu;
import com.openminds.app.database.entity.Formation;
import com.openminds.openminds.R;

import java.util.ArrayList;

public class ContenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenu);

        // Bouton retour toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        int formationId = getIntent().getIntExtra("formationId", -1);

        TextView tvTitre = findViewById(R.id.tv_titre_formation);
        TextView tvThematique = findViewById(R.id.tv_thematique);
        RecyclerView recycler = findViewById(R.id.recycler_modules);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Contenu> liste = new ArrayList<>();
        ContenuAdapter adapter = new ContenuAdapter(liste, contenu -> {
            // clic sur un module → toast pour l'instant
            android.widget.Toast.makeText(this, "Module : " + contenu.getTitre(), android.widget.Toast.LENGTH_SHORT).show();
        });
        recycler.setAdapter(adapter);

        // Charger la formation + ses modules depuis la DB
        AppDatabase db = AppDatabase.getInstance(getApplication());

        db.formationDao().getFormationById(formationId).observe(this, formation -> {
            if (formation != null) {
                tvTitre.setText(formation.getTitre());
                tvThematique.setText(formation.getThematique());
            }
        });

        db.contenuDao().getContenusByFormation(formationId).observe(this, contenus -> {
            liste.clear();
            if (contenus != null) liste.addAll(contenus);
            adapter.notifyDataSetChanged();
        });
    }
}