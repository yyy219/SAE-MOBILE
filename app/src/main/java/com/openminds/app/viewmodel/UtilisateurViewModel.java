package com.openminds.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.openminds.app.database.entity.Inscription;
import com.openminds.app.database.entity.Telechargement;
import com.openminds.app.database.entity.Utilisateur;
import com.openminds.app.repository.UtilisateurRepository;

import java.util.List;

public class UtilisateurViewModel extends AndroidViewModel {

    private final UtilisateurRepository repository;

    // LiveData pour l'utilisateur connecté — observée par l'UI
    private final MutableLiveData<Utilisateur> utilisateurConnecte = new MutableLiveData<>();

    // LiveData pour le résultat du login (true = succès, false = échec)
    private final MutableLiveData<Boolean> loginResultat = new MutableLiveData<>();

    // LiveData pour savoir si l'email existe déjà
    private final MutableLiveData<Boolean> emailDejaUtilise = new MutableLiveData<>();

    public UtilisateurViewModel(@NonNull Application application) {
        super(application);
        repository = new UtilisateurRepository(application);
    }

    // ── Getters LiveData (observés par l'Activity) ────────

    public LiveData<Utilisateur> getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public LiveData<Boolean> getLoginResultat() {
        return loginResultat;
    }

    public LiveData<Boolean> getEmailDejaUtilise() {
        return emailDejaUtilise;
    }

    // ── Actions appelées depuis l'Activity ────────────────

    // Tentative de connexion (US01)
    public void login(String email, String motDePasse) {
        repository.login(email, motDePasse, utilisateur -> {
            utilisateurConnecte.postValue(utilisateur);
            loginResultat.postValue(utilisateur != null);
        });
    }

    // Créer un compte (US01)
    public void creerCompte(Utilisateur utilisateur) {
        // Vérifie d'abord si l'email est libre
        repository.emailExiste(utilisateur.getEmail(), existe -> {
            if (existe) {
                emailDejaUtilise.postValue(true);
            } else {
                emailDejaUtilise.postValue(false);
                repository.inscrireUtilisateur(utilisateur);
            }
        });
    }

    // FIX #6 : méthode manquante appelée par MonEspaceActivity
    public LiveData<Utilisateur> getUtilisateurById(int id) {
        return repository.getUtilisateurById(id);
    }

    public void updateProfil(Utilisateur utilisateur) {
        repository.updateUtilisateur(utilisateur);
    }

    public LiveData<List<Utilisateur>> getAllUtilisateurs() {
        return repository.getAllUtilisateurs();
    }

    public LiveData<List<Utilisateur>> getBenevoles() {
        return repository.getBenevoles();
    }

    // ── Inscriptions sessions (US03) ──────────────────────

    public void sInscrireSession(int utilisateurId, int sessionId) {
        repository.isDejaInscrit(utilisateurId, sessionId, dejaInscrit -> {
            if (!dejaInscrit) {
                Inscription inscription = new Inscription();

                // On utilise les SETTERS pour modifier les valeurs privées
                inscription.setUtilisateurId(utilisateurId);
                inscription.setSessionId(sessionId);

                // Correction : on utilise setDateInscription (ou le nom de votre champ date)
                inscription.setDateInscription(new java.util.Date().toString());
                inscription.setTimestampInscription(System.currentTimeMillis());

                inscription.setStatut("inscrit");
                inscription.setProgressionPourcentage(0);

                // On envoie l'objet complet au repository pour l'insertion
                repository.inscrireSession(inscription);
            }
        });
    }

    public LiveData<List<Inscription>> getMesInscriptions(int utilisateurId) {
        return repository.getMesInscriptions(utilisateurId);
    }

    // ── Téléchargements (US05) ────────────────────────────

    public void telechargerFormation(int utilisateurId, int formationId) {
        repository.isDejaTelechargee(utilisateurId, formationId, deja -> {
            if (!deja) {
                Telechargement t = new Telechargement();
                t.setUtilisateurId(utilisateurId);
                t.setFormationId(formationId);
                t.setDateTelecharge(new java.util.Date().toString());
                repository.telechargerFormation(t);
            }
        });
    }

    public LiveData<List<Telechargement>> getMesTelechargements(int utilisateurId) {
        return repository.getMesTelechargements(utilisateurId);
    }

    public void supprimerTelechargement(int utilisateurId, int formationId) {
        repository.supprimerTelechargement(utilisateurId, formationId);
    }
}