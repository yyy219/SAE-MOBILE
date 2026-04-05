package com.openminds.app.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.openminds.openminds.R;

public class FormationActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_formations) {

            } else if (id == R.id.nav_profil) {
                Toast.makeText(this, "Module Profil bientôt disponible", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_progression) {
                Toast.makeText(this, "Module Progression bientôt disponible", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FormationFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_formations);
        }
    }
}