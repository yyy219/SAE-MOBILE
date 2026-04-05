package com.openminds.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.openminds.app.database.entity.Contenu;
import com.openminds.app.repository.ContenuRepository;

public class ContenuViewModel extends AndroidViewModel {
    private final ContenuRepository repository;

    public ContenuViewModel(@NonNull Application application) {
        super(application);
        repository = new ContenuRepository(application);
    }

    public LiveData<Contenu> getContenuById(int id) {
        return repository.getContenuById(id);
    }
}
