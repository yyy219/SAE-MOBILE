package com.openminds.app.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.openminds.app.database.AppDatabase;
import com.openminds.app.database.dao.ContenuDao;
import com.openminds.app.database.entity.Contenu;

public class ContenuRepository {
    private final ContenuDao contenuDao;

    public ContenuRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        contenuDao = db.contenuDao();
    }

    public LiveData<Contenu> getContenuById(int id) {
        return contenuDao.getContenuById(id);
    }
}
