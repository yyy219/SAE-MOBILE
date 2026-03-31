package com.openminds.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Telechargement;
import java.util.List;

@Dao
public interface TelechargementDao {

    // Enregistre un nouveau téléchargement
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Telechargement telechargement);

    // Supprime un téléchargement (si l'user supprime la formation offline)
    @Delete
    void delete(Telechargement telechargement);

    // Toutes les formations téléchargées par un utilisateur
    @Query("SELECT * FROM telechargement WHERE utilisateurId = :utilisateurId")
    LiveData<List<Telechargement>> getTelechargementsByUtilisateur(int utilisateurId);

    // Vérifie si une formation est déjà téléchargée par cet utilisateur
    // Retourne 1 si oui, 0 si non
    @Query("SELECT COUNT(*) FROM telechargement " +
            "WHERE utilisateurId = :uid AND formationId = :fid")
    int isDejaTelechargee(int uid, int fid);

    // Supprime un téléchargement précis par userId + formationId
    @Query("DELETE FROM telechargement " +
            "WHERE utilisateurId = :uid AND formationId = :fid")
    void deleteByUserAndFormation(int uid, int fid);


    @Query("SELECT formationId FROM telechargement WHERE utilisateurId = :userId")
    LiveData<List<Integer>> getFormationIdsTelechargeesLive(int userId);

    @Query("SELECT formationId FROM telechargement WHERE utilisateurId = :userId")
    List<Integer> getFormationIdsTelechargeesSync(int userId);

    @Query("SELECT f.* FROM formation f " +
            "INNER JOIN telechargement t ON f.id = t.formationId " +
            "WHERE t.utilisateurId = :userId")
    LiveData<List<Formation>> getFormationsTelechargeesLive(int userId);

}