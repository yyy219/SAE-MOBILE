package com.openminds.app.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.openminds.app.database.AppDatabase;
import com.openminds.app.database.dao.FormationDao;
import com.openminds.app.database.dao.TelechargementDao;
import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Telechargement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelechargementRepository {

    private final TelechargementDao telechargementDao;
    private final FormationDao formationDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TelechargementRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        telechargementDao = db.telechargementDao();
        formationDao      = db.formationDao();
    }

    public void telecharger(int userId, int formationId) {
        executor.execute(() -> {
            Telechargement t = new Telechargement();
            t.setUtilisateurId(userId);
            t.setFormationId(formationId);
            t.setDateTelecharge(
                    new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(new Date())
            );
            telechargementDao.insert(t);
        });
    }

    public void supprimerTelechargement(int userId, int formationId) {
        executor.execute(() ->
                telechargementDao.deleteByUserAndFormation(userId, formationId)
        );
    }

    // ✅ CORRECTION : version LiveData pour éviter l'appel synchrone sur le main thread
    public LiveData<List<Integer>> getFormationIdsTelechargeesLive(int userId) {
        return telechargementDao.getFormationIdsTelechargeesLive(userId);
    }


    // ⚠️ Cette méthode ne doit être appelée QUE depuis un thread background
    public boolean isDejaTelechargeeBackground(int userId, int formationId) {
        return telechargementDao.isDejaTelechargee(userId, formationId) > 0;
    }

}