package com.openminds.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.openminds.app.database.entity.Contenu;
import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Session;
import com.openminds.app.repository.FormationRepository;

import java.util.List;

public class FormationViewModel extends AndroidViewModel {

    private final FormationRepository repository;
    public final LiveData<List<Formation>> toutesLesFormations;

    public FormationViewModel(@NonNull Application application) {
        super(application);
        repository = new FormationRepository(application);
        toutesLesFormations = repository.getAllFormations();
    }

    public void ajouterFormation(Formation f)    { repository.insert(f); }
    public void modifierFormation(Formation f)   { repository.update(f); }
    public void supprimerFormation(Formation f)  { repository.delete(f); }

    public LiveData<List<Formation>> getFormationsInscrites(int userId) {
        return repository.getFormationsInscrites(userId);
    }

    public LiveData<Formation> getFormationById(int id) {
        return repository.getFormationById(id);
    }

    public LiveData<List<Session>> getSessionsDeLaFormation(int formationId) {
        return repository.getSessionsByFormation(formationId);
    }

    public void ajouterSession(Session s) { repository.insertSession(s); }

    public void enregistrerFormationComplete(Formation f, List<Session> sessions) {
        repository.insererFormationEtSessions(f, sessions);
    }

    public interface InsertCallback {
        void onInserted(long formationId);
    }

    public void ajouterFormationAvecSessions(
            Formation formation,
            List<Session> sessions,
            InsertCallback callback) {
        repository.insertFormationAvecSessions(formation, sessions, callback);
    }

    public void ajouterFormationAvecSessionsEtModules(
            Formation formation,
            List<Session> sessions,
            List<Contenu> modules,
            InsertCallback callback) {
        repository.insertFormationAvecSessionsEtModules(formation, sessions, modules, callback);
    }
}