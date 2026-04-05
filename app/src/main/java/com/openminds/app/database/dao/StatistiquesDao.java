package com.openminds.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.FormationTop;
import com.openminds.app.database.entity.StatThematique;
import java.util.List;

@Dao
public interface StatistiquesDao {



    @Query("SELECT COUNT(*) FROM formation")
    LiveData<Integer> countFormations();

    @Query("SELECT COUNT(*) FROM session")
    LiveData<Integer> countSessions();




    @Query("SELECT COUNT(DISTINCT utilisateurId) FROM inscription " +
            "WHERE timestampInscription >= :debut")
    LiveData<Integer> countBenevolesActifs(long debut);


    // NULLIF évite la division par zéro
    @Query("SELECT CAST(SUM(scoreObtenu) AS REAL) * 100 " +
            "/ NULLIF(SUM(scoreMax), 0) " +
            "FROM resultat_quiz WHERE timestampPassage >= :debut")
    LiveData<Float> getTauxReussite(long debut);

    // Participation par thématique sur la période
    // Jointure obligatoire : inscription → session → formation
    @Query("SELECT f.thematique, COUNT(i.id) AS nbInscrits " +
            "FROM inscription i " +
            "INNER JOIN session s ON i.sessionId = s.id " +
            "INNER JOIN formation f ON s.formationId = f.id " +
            "WHERE i.timestampInscription >= :debut " +
            "GROUP BY f.thematique " +
            "ORDER BY nbInscrits DESC")
    LiveData<List<StatThematique>> getParticipationParThematique(long debut);


    @Query("SELECT f.id AS formationId, f.titre, COUNT(i.id) AS nbInscrits " +
            "FROM inscription i " +
            "INNER JOIN session s ON i.sessionId = s.id " +
            "INNER JOIN formation f ON s.formationId = f.id " +
            "WHERE i.timestampInscription >= :debut " +
            "GROUP BY f.id " +
            "ORDER BY nbInscrits DESC " +
            "LIMIT :limit")
    LiveData<List<FormationTop>> getTopFormations(long debut, int limit);

    @Query("SELECT f.* FROM formation f " +
            "INNER JOIN session s ON f.id = s.formationId " +
            "INNER JOIN inscription i ON s.id = i.sessionId " +
            "WHERE i.utilisateurId = :userId AND i.progressionPourcentage >= 100")
    LiveData<List<Formation>> getFormationsTerminees(int userId);
}