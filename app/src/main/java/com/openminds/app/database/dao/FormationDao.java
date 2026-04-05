package com.openminds.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.openminds.app.database.entity.Formation;
import java.util.List;

@Dao
public interface FormationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Formation formation);

    @Update
    void update(Formation formation);

    @Delete
    void delete(Formation formation);

    @Query("SELECT * FROM formation ORDER BY dateCreation DESC")
    LiveData<List<Formation>> getAllFormations();

    @Query("SELECT * FROM formation WHERE id = :formationId")
    LiveData<Formation> getFormationById(int formationId);

    @Query("SELECT * FROM formation WHERE thematique = :thematique")
    LiveData<List<Formation>> getFormationsByThematique(String thematique);

    @Query("SELECT * FROM formation WHERE id IN (:ids)")
    LiveData<List<Formation>> getFormationsByIds(List<Integer> ids);


    @Query("SELECT DISTINCT f.* FROM formation f " +
            "INNER JOIN session s ON s.formationId = f.id " +
            "INNER JOIN inscription i ON i.sessionId = s.id " +
            "WHERE i.utilisateurId = :userId " +
            "ORDER BY i.timestampInscription DESC")
    LiveData<List<Formation>> getFormationsInscritesParUtilisateur(int userId);
}