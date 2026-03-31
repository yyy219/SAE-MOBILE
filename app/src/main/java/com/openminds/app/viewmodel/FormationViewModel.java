package com.openminds.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Session;
import com.openminds.app.repository.FormationRepository;

import java.util.List;

//Le ViewModel survit aux rotations d'écran, contrairement à une Activity qui est recréée.
// Il expose les données via LiveData, un objet observable :
// quand la donnée change en base, l'interface se met à jour automatiquement sans code supplémentaire.
// C'est le principe de l'architecture MVVM — Model View ViewModel

//contient aucune requête SQL. Il se contente d'appeler le repository.

public class FormationViewModel extends AndroidViewModel {

    private final FormationRepository repository;

    // LiveData publique : l'Activity peut l'observer
    public final LiveData<List<Formation>> toutesLesFormations;

    public FormationViewModel(@NonNull Application application) {
        super(application);
        repository = new FormationRepository(application);
        // Chargement automatique dès la création du ViewModel
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

// Interface callback — permet de notifier l'Activity
// quand l'insertion est terminée en thread secondaire
// L'Activity reçoit l'id généré automatiquement par Room

    public interface InsertCallback {
        void onInserted(long formationId);
    }


// Sauvegarde la Formation ET toutes ses Sessions
// en une seule opération coordonnée

    public void ajouterFormationAvecSessions(
            Formation formation,
            List<Session> sessions,
            InsertCallback callback) {

        repository.insertFormationAvecSessions(
                formation, sessions, callback);
    }
}