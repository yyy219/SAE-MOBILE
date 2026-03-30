package com.openminds.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.openminds.app.database.entity.Inscription;
import java.util.List;

@Dao
public interface InscriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Inscription inscription);

    @Update
    void update(Inscription inscription);

    @Delete
    void delete(Inscription inscription);

    @Query("SELECT * FROM inscription WHERE utilisateurId = :utilisateurId")
    LiveData<List<Inscription>> getInscriptionsByUtilisateur(int utilisateurId);

    @Query("SELECT * FROM inscription WHERE sessionId = :sessionId")
    LiveData<List<Inscription>> getInscriptionsBySession(int sessionId);

    // Retourne 1 si déjà inscrit, 0 sinon
    @Query("SELECT COUNT(*) FROM inscription " +
            "WHERE utilisateurId = :uid AND sessionId = :sid")
    int isDejaInscrit(int uid, int sid);

    @Query("SELECT COUNT(*) FROM inscription WHERE sessionId = :sessionId")
    int getNombreInscrits(int sessionId);
}