package com.openminds.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.openminds.app.database.entity.Formation;
import com.openminds.app.repository.TelechargementRepository;

import java.util.List;

public class TelechargementViewModel extends AndroidViewModel {

    private final TelechargementRepository repository;

    public TelechargementViewModel(@NonNull Application application) {
        super(application);
        repository = new TelechargementRepository(application);
    }

    public void telecharger(int userId, int formationId) {
        repository.telecharger(userId, formationId);
    }

    public void supprimerTelechargement(int userId, int formationId) {
        repository.supprimerTelechargement(userId, formationId);
    }


    // ✅ Nouvelle méthode : retourne les IDs sous forme LiveData (safe main thread)
    public LiveData<List<Integer>> getFormationIdsTelechargeesLive(int userId) {
        return repository.getFormationIdsTelechargeesLive(userId);
    }
}