package com.openminds.app.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.openminds.app.database.AppDatabase;
import com.openminds.app.database.dao.InscriptionDao;
import com.openminds.app.database.dao.SessionDao;
import com.openminds.app.database.entity.Inscription;
import com.openminds.app.database.entity.Session;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InscriptionRepository {

    private final InscriptionDao inscriptionDao;
    private final SessionDao sessionDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface InscriptionCallback {
        void onResult(boolean succes, String message);
    }

    public InscriptionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        inscriptionDao = db.inscriptionDao();
        sessionDao = db.sessionDao();
    }

    public LiveData<List<Session>> getSessionsByFormation(int formationId) {
        return sessionDao.getSessionsByFormation(formationId);
    }

    public int getNombreInscrits(int sessionId) {
        return inscriptionDao.getNombreInscrits(sessionId);
    }

    public void inscrire(int utilisateurId, int sessionId, InscriptionCallback callback) {
        executor.execute(() -> {
            int dejaInscrit = inscriptionDao.isDejaInscrit(utilisateurId, sessionId);
            if (dejaInscrit > 0) {
                if (callback != null) callback.onResult(false, "Vous êtes déjà inscrit à cette session.");
                return;
            }

            Session session = sessionDao.getSessionByIdSync(sessionId);
            if (session == null) {
                if (callback != null) callback.onResult(false, "Session introuvable.");
                return;
            }

            int inscrits = inscriptionDao.getNombreInscrits(sessionId);
            if (inscrits >= session.getPlacesMax()) {
                if (callback != null) callback.onResult(false, "Cette session est complète.");
                return;
            }

            Inscription inscription = new Inscription();
            inscription.setUtilisateurId(utilisateurId);
            inscription.setSessionId(sessionId);
            inscription.setStatut("inscrit");
            inscription.setProgressionPourcentage(0);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            inscription.setDateInscription(date);
            inscription.setTimestampInscription(System.currentTimeMillis());

            inscriptionDao.insert(inscription);

            if (callback != null) callback.onResult(true, "Inscription réussie !");
        });
    }
}