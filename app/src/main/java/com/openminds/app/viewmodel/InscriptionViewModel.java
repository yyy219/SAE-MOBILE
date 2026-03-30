package com.openminds.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.openminds.app.database.entity.Session;
import com.openminds.app.repository.InscriptionRepository;

import java.util.List;

public class InscriptionViewModel extends AndroidViewModel {

    private final InscriptionRepository repository;
    private final MutableLiveData<String> messageInscription = new MutableLiveData<>();

    public InscriptionViewModel(@NonNull Application application) {
        super(application);
        repository = new InscriptionRepository(application);
    }

    public LiveData<List<Session>> getSessionsByFormation(int formationId) {
        return repository.getSessionsByFormation(formationId);
    }

    public LiveData<String> getMessageInscription() {
        return messageInscription;
    }

    public int getNombreInscrits(int sessionId) {
        return repository.getNombreInscrits(sessionId);
    }

    public void inscrire(int utilisateurId, int sessionId) {
        repository.inscrire(utilisateurId, sessionId, (succes, message) ->
                messageInscription.postValue(message));
    }
}