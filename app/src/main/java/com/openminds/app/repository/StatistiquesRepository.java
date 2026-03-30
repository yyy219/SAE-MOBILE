package com.openminds.app.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.openminds.app.database.AppDatabase;
import com.openminds.app.database.dao.StatistiquesDao;
import com.openminds.app.database.entity.FormationTop;
import com.openminds.app.database.entity.StatThematique;
import java.util.List;

public class StatistiquesRepository {

    private final StatistiquesDao dao;

    public StatistiquesRepository(Application application) {
        dao = AppDatabase.getInstance(application).statistiquesDao();
    }

    public LiveData<Integer> getNbFormations() {
        return dao.countFormations();
    }

    public LiveData<Integer> getNbSessions() {
        return dao.countSessions();
    }

    public LiveData<Integer> getNbBenevolesActifs(long debut) {
        return dao.countBenevolesActifs(debut);
    }

    public LiveData<Float> getTauxReussite(long debut) {
        return dao.getTauxReussite(debut);
    }

    public LiveData<List<StatThematique>> getParticipationParThematique(long debut) {
        return dao.getParticipationParThematique(debut);
    }

    public LiveData<List<FormationTop>> getTopFormations(long debut, int limit) {
        return dao.getTopFormations(debut, limit);
    }
}