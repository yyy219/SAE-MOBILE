package com.openminds.app.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.openminds.app.database.AppDatabase;
import com.openminds.app.database.dao.ContenuDao;
import com.openminds.app.database.dao.FormationDao;
import com.openminds.app.database.dao.SessionDao;
import com.openminds.app.database.entity.Contenu;
import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Session;
import com.openminds.app.viewmodel.FormationViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FormationRepository {

    private final FormationDao formationDao;
    private final SessionDao sessionDao;
    private final ContenuDao contenuDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FormationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        formationDao = db.formationDao();
        sessionDao   = db.sessionDao();
        contenuDao   = db.contenuDao();
    }

    public LiveData<List<Formation>> getAllFormations() {
        return formationDao.getAllFormations();
    }

    public LiveData<List<Formation>> getFormationsInscrites(int userId) {
        return formationDao.getFormationsInscritesParUtilisateur(userId);
    }

    public LiveData<Formation> getFormationById(int id) {
        return formationDao.getFormationById(id);
    }

    public void insert(Formation formation) {
        executor.execute(() -> formationDao.insert(formation));
    }

    public void update(Formation formation) {
        executor.execute(() -> formationDao.update(formation));
    }

    public void delete(Formation formation) {
        executor.execute(() -> formationDao.delete(formation));
    }

    public LiveData<List<Session>> getSessionsByFormation(int formationId) {
        return sessionDao.getSessionsByFormation(formationId);
    }

    public void insertSession(Session session) {
        executor.execute(() -> sessionDao.insert(session));
    }

    public void deleteSession(Session session) {
        executor.execute(() -> sessionDao.delete(session));
    }

    public void insererFormationEtSessions(Formation formation, List<Session> sessions) {
        executor.execute(() -> {
            long formationId = formationDao.insert(formation);
            for (Session session : sessions) {
                session.setFormationId((int) formationId);
                sessionDao.insert(session);
            }
        });
    }

    public void insertFormationAvecSessions(
            Formation formation,
            List<Session> sessions,
            FormationViewModel.InsertCallback callback) {

        executor.execute(() -> {
            long formationId = formationDao.insert(formation);
            for (Session session : sessions) {
                session.setFormationId((int) formationId);
                sessionDao.insert(session);
            }
            if (callback != null) callback.onInserted(formationId);
        });
    }

    public void insertFormationAvecSessionsEtModules(
            Formation formation,
            List<Session> sessions,
            List<Contenu> modules,
            FormationViewModel.InsertCallback callback) {

        executor.execute(() -> {
            long formationId = formationDao.insert(formation);

            for (Session session : sessions) {
                session.setFormationId((int) formationId);
                sessionDao.insert(session);
            }

            for (int i = 0; i < modules.size(); i++) {
                Contenu m = modules.get(i);
                m.setFormationId((int) formationId);
                m.setOrdre(i + 1);
                contenuDao.insert(m);
            }

            if (callback != null) callback.onInserted(formationId);
        });
    }
}