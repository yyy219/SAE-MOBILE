package com.openminds.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.openminds.openminds.R;
import com.openminds.app.viewmodel.StatistiquesViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("OpenMindsPrefs", Context.MODE_PRIVATE);


        StatistiquesViewModel statsVm = new ViewModelProvider(this).get(StatistiquesViewModel.class);

        TextView tvFormations = findViewById(R.id.tvAdminNbFormations);
        TextView tvBenevoles  = findViewById(R.id.tvAdminNbBenevoles);
        TextView tvTaux       = findViewById(R.id.tvAdminTaux);

        statsVm.nbFormations.observe(this, nb -> {
            if (tvFormations != null && nb != null)
                tvFormations.setText(String.valueOf(nb));
        });

        statsVm.nbBenevolesActifs.observe(this, nb -> {
            if (tvBenevoles != null && nb != null)
                tvBenevoles.setText(String.valueOf(nb));
        });

        statsVm.tauxReussite.observe(this, taux -> {
            if (tvTaux != null)
                tvTaux.setText(taux != null ? Math.round(taux) + "%" : "—");
        });


        findViewById(R.id.btnNouvelleFormation).setOnClickListener(v ->
                startActivity(new Intent(this, NouvelleFormationActivity.class)));

        findViewById(R.id.btnStatistiques).setOnClickListener(v ->
                startActivity(new Intent(this, StatistiquesActivity.class)));

        findViewById(R.id.btnChoisirSession).setOnClickListener(v ->
                startActivity(new Intent(this, CatalogueFormationsActivity.class)));

        findViewById(R.id.btnVoirCatalogue).setOnClickListener(v ->
                startActivity(new Intent(this, CatalogueFormationsActivity.class)));


        findViewById(R.id.btnDeconnexionMain).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ConnexionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}