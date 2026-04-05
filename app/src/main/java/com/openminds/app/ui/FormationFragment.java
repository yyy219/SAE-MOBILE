package com.openminds.app.ui;

import android.app.Application;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.openminds.openminds.R;
import com.openminds.app.database.entity.Formation;
import com.openminds.app.repository.FormationRepository;

import java.util.ArrayList;
import java.util.List;

public class FormationFragment extends Fragment {

    private FormationAdapter adapter;
    private List<Formation> formationList;
    private List<Formation> formationListFull;

    public FormationFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_formations, container, false);

        ListView lvFormations = rootView.findViewById(R.id.lv_habitats_frag);
        EditText etRecherche = rootView.findViewById(R.id.et_recherche);

        formationList = new ArrayList<>();
        formationListFull = new ArrayList<>();
        adapter = new FormationAdapter(getActivity(), R.layout.item_formation, formationList);
        lvFormations.setAdapter(adapter);

        chargerFormationsDepuisBDD();

        etRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrerFormations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        lvFormations.setOnItemClickListener((parent, view, position, id) -> {
            Formation selected = formationList.get(position);
            Toast.makeText(getContext(), "Ouverture : " + selected.getTitre(), Toast.LENGTH_SHORT).show();
        });

        return rootView;
    }

    private void filtrerFormations(String texteRecherche) {
        List<Formation> listeFiltree = new ArrayList<>();
        for (Formation f : formationListFull) {
            if (f.getTitre().toLowerCase().contains(texteRecherche.toLowerCase()) ||
                    f.getThematique().toLowerCase().contains(texteRecherche.toLowerCase())) {
                listeFiltree.add(f);
            }
        }
        formationList.clear();
        formationList.addAll(listeFiltree);
        adapter.notifyDataSetChanged();
    }

    private void chargerFormationsDepuisBDD() {

        FormationRepository repo = new FormationRepository(requireActivity().getApplication());


        repo.getAllFormations().observe(getViewLifecycleOwner(), formationsBDD -> {

            if (formationsBDD != null) {

                formationListFull.clear();
                formationListFull.addAll(formationsBDD);

                formationList.clear();
                formationList.addAll(formationListFull);


                adapter.notifyDataSetChanged();


                if (formationsBDD.isEmpty()) {
                    Log.d("BDD_TEST", "La base de données de Bassirou est vide pour le moment.");
                }
            }
        });
    }
}