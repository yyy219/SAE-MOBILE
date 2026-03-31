package com.openminds.app.ui;

import com.openminds.openminds.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openminds.app.viewmodel.TelechargementViewModel;

public class MesformationsActivity extends AppCompatActivity {

    private TelechargementViewModel viewModel;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_formations);

        currentUserId = getSharedPreferences("session", MODE_PRIVATE)
                .getInt("userId", -1);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMesFormations);
        TextView tvEmpty          = findViewById(R.id.tvEmptyMesFormations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CatalogueAdapter adapter = new CatalogueAdapter(formation -> {
            Intent intent = new Intent(this, FormationActivity.class);
            intent.putExtra("formationId", formation.getId());
            intent.putExtra("offline", true);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(TelechargementViewModel.class);

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