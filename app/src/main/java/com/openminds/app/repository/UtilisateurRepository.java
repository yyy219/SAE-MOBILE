package com.openminds.app.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.openminds.app.database.AppDatabase;
import com.openminds.app.database.dao.InscriptionDao;
import com.openminds.app.database.dao.TelechargementDao;
import com.openminds.app.database.dao.UtilisateurDao;
import com.openminds.app.database.entity.Inscription;
import com.openminds.app.database.entity.Telechargement;
import com.openminds.app.database.entity.Utilisateur;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UtilisateurRepository {

    private final UtilisateurDao utilisateurDao;
    private final InscriptionDao inscriptionDao;
    private final TelechargementDao telechargementDao;


    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UtilisateurRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        utilisateurDao   = db.utilisateurDao();
        inscriptionDao   = db.inscriptionDao();
        telechargementDao = db.telechargementDao();
    }




    public void inscrireUtilisateur(Utilisateur utilisateur) {
        executor.execute(() -> utilisateurDao.insert(utilisateur));
    }


    public interface LoginCallback {
        void onResult(Utilisateur utilisateur);
    }

    public void login(String email, String motDePasse, LoginCallback callback) {
        executor.execute(() -> {
            Utilisateur u = utilisateurDao.login(email, motDePasse);
            callback.onResult(u);
        });
    }


    public interface EmailCallback {
        void onResult(boolean existe);
    }

    public void emailExiste(String email, EmailCallback callback) {
        executor.execute(() -> {
            int count = utilisateurDao.emailExiste(email);
            callback.onResult(count > 0);
        });
    }

    public void updateUtilisateur(Utilisateur utilisateur) {
        executor.execute(() -> utilisateurDao.update(utilisateur));
    }


    public LiveData<Utilisateur> getUtilisateurById(int id) {
        return utilisateurDao.getUtilisateurById(id);
    }

    public LiveData<List<Utilisateur>> getAllUtilisateurs() {
        return utilisateurDao.getAllUtilisateurs();
    }

    public LiveData<List<Utilisateur>> getBenevoles() {
        return utilisateurDao.getBenevoles();
    }




    public void inscrireSession(Inscription inscription) {
        executor.execute(() -> inscriptionDao.insert(inscription));
    }

    public LiveData<List<Inscription>> getMesInscriptions(int utilisateurId) {
        return inscriptionDao.getInscriptionsByUtilisateur(utilisateurId);
    }

    // Vérifie si déjà inscrit avant d'inscrire (US03)
    public interface InscritCallback {
        void onResult(boolean dejaInscrit);
    }

    public void isDejaInscrit(int uid, int sid, InscritCallback callback) {
        executor.execute(() -> {
            int count = inscriptionDao.isDejaInscrit(uid, sid);
            callback.onResult(count > 0);
        });
    }




    public void telechargerFormation(Telechargement t) {
        executor.execute(() -> telechargementDao.insert(t));
    }


    public void supprimerTelechargement(int uid, int fid) {
        executor.execute(() -> telechargementDao.deleteByUserAndFormation(uid, fid));
    }

    public LiveData<List<Telechargement>> getMesTelechargements(int utilisateurId) {
        return telechargementDao.getTelechargementsByUtilisateur(utilisateurId);
    }


    public interface TelechargementCallback {
        void onResult(boolean dejaTelechargee);
    }

    public void isDejaTelechargee(int uid, int fid, TelechargementCallback callback) {
        executor.execute(() -> {
            int count = telechargementDao.isDejaTelechargee(uid, fid);
            callback.onResult(count > 0);
        });
    }
}