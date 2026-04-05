package com.openminds.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.openminds.app.database.entity.Contenu;
import java.util.List;

//Le DAO est une interface qui sert de pont entre ton code Java et ta base de données SQLite//
// Choix de room car solution officielle Google pour Android qui utilise SQlLite
//Local donc pas besoin de connexion internet idela pour app mobile//
//Couche d'abrastraction sur Sqlite permet verification requetes et evite erreurs sql //

@Dao
public interface ContenuDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Contenu contenu);

    @Update
    void update(Contenu contenu);

    @Delete
    void delete(Contenu contenu);

    // Triés par ordre d'affichage (1er module, 2ème module...)
    @Query("SELECT * FROM contenu WHERE formationId = :formationId ORDER BY ordre ASC")
    LiveData<List<Contenu>> getContenusByFormation(int formationId);

    // Filtre par type : "texte", "video" ou "quiz"
    @Query("SELECT * FROM contenu WHERE formationId = :formationId AND type = :type")
    LiveData<List<Contenu>> getContenusByType(int formationId, String type);

    @Query("SELECT * FROM contenu WHERE id = :id")
    LiveData<Contenu> getContenuById(int id);

    //LiveData permet mise a jour de la BD afficher sur l'ecran en temps reel sans que rafrachir (ex ajout/supression contenu)//
}