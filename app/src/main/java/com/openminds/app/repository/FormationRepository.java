package com.openminds.app.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.openminds.app.database.AppDatabase;
import com.openminds.app.database.dao.FormationDao;
import com.openminds.app.database.dao.SessionDao;
import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Session;
import com.openminds.app.viewmodel.FormationViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//Le Repository est un patron de conception qui isole la logique d'accès aux données du reste de l'application.
// Le ViewModel ne parle qu'au Repository, et le Repository parle à la base de données.
// Si demain on veut ajouter une API distante,
// on ne modifie que le Repository, pas le ViewModel ni l'UI.

//facilite interface graphique abstaction ecran et code xml ne doivent pas savoir comment fonctionne BD
//"Hé, donne-moi les formations !" ou "Sauvegarde cette formation !".
//Le Repository s'occupe de la logistique en appelant les bons DAO (FormationDao, SessionDao).
public class FormationRepository {

    private final FormationDao formationDao;
    private final SessionDao sessionDao;

    // Thread séparé OBLIGATOIRE : Room interdit les opérations d'écriture sur le thread principal (celui de l'UI)
    // interdiction d'ecrire sur mainthread au risque de faire geler l'ecran d'affichage
    //executor travaille d'arriere plan //

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FormationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        formationDao = db.formationDao();
        sessionDao   = db.sessionDao();
    }

    // ── Formations ──────────────────────────────────────

    public LiveData<List<Formation>> getAllFormations() {
        return formationDao.getAllFormations();  // Room gère le thread car il voit le LiveData
    }

    public LiveData<Formation> getFormationById(int id) {
        return formationDao.getFormationById(id);
    }

    public void insert(Formation formation) {
        // Lambda exécutée dans le thread secondaire
        executor.execute(() -> formationDao.insert(formation));
    }

    public void update(Formation formation) {
        executor.execute(() -> formationDao.update(formation));
    }

    public void delete(Formation formation) {
        executor.execute(() -> formationDao.delete(formation));
    }


    

    // ── Sessions ─────────────────────────────────────────

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
            // 1. On insère la formation et on récupère son ID généré
            long formationIdGénéré = formationDao.insert(formation);

            // 2. On attribue cet ID à toutes les sessions associées et on les insère
            for (Session session : sessions) {
                session.setFormationId((int) formationIdGénéré);
                sessionDao.insert(session);
            }
        });

    }

    public void insertFormationAvecSessions(
            Formation formation,
            List<Session> sessions,
            FormationViewModel.InsertCallback callback) {

        executor.execute(() -> {

            // 1. Insère la formation
            //    Room retourne automatiquement l'id généré
            long formationId = formationDao.insert(formation);

            // 2. Pour chaque session → affecte le bon formationId
            //    et insère en BD
            for (Session session : sessions) {
                // SETTER — lie la session à sa formation
                session.setFormationId((int) formationId);
                sessionDao.insert(session);
            }

            // 3. Notifie l'Activity que tout est sauvegardé
            if (callback != null) {
                callback.onInserted(formationId);
            }
        });
    }

}