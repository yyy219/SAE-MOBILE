package com.openminds.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.openminds.openminds.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnNouvelleFormation = findViewById(R.id.btnNouvelleFormation);
        btnNouvelleFormation.setOnClickListener(v -> {
            startActivity(new Intent(this, NouvelleFormationActivity.class));
        });

        findViewById(R.id.btnStatistiques).setOnClickListener(v -> {
            startActivity(new Intent(this, StatistiquesActivity.class));
        });

        // Ouvre le catalogue → bénévole choisit une formation puis une session
        findViewById(R.id.btnChoisirSession).setOnClickListener(v -> {
            startActivity(new Intent(this, CatalogueFormationsActivity.class));
        });
    }
}