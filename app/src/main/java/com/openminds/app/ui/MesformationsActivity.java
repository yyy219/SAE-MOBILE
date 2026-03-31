package com.openminds.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.app.viewmodel.TelechargementViewModel;
import com.openminds.openminds.R;

public class MesformationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_formations);

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());

        int currentUserId = getSharedPreferences("OpenMindsPrefs", MODE_PRIVATE)
                .getInt("connected_user_id", -1);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMesFormations);
        TextView tvEmpty          = findViewById(R.id.tvEmptyMesFormations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CatalogueAdapter adapter = new CatalogueAdapter(formation -> {
            Intent intent = new Intent(this, ContenuActivity.class);
            intent.putExtra("formationId", formation.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        TelechargementViewModel viewModel = new ViewModelProvider(this)
                .get(TelechargementViewModel.class);

        viewModel.getFormationsTelechargeesLive(currentUserId).observe(this, formations -> {
            if (formations == null || formations.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
                adapter.setFormations(formations);
            }
        });
    }
}