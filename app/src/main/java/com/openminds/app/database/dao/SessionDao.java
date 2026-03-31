package com.openminds.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.openminds.app.database.entity.Session;
import java.util.List;

@Dao
public interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Session session);

    @Update
    void update(Session session);

    @Delete
    void delete(Session session);

    @Query("SELECT * FROM session WHERE formationId = :formationId ORDER BY dateDebut ASC")
    LiveData<List<Session>> getSessionsByFormation(int formationId);

    @Query("SELECT * FROM session WHERE id = :sessionId")
    LiveData<Session> getSessionById(int sessionId);

    // Version synchrone (utilisée dans les threads background)
    @Query("SELECT * FROM session WHERE id = :sessionId LIMIT 1")
    Session getSessionByIdSync(int sessionId);

    // Places restantes = placesMax - nombre d'inscrits
    @Query("SELECT s.placesMax - COUNT(i.id) " +
            "FROM session s LEFT JOIN inscription i ON i.sessionId = s.id " +
            "WHERE s.id = :sessionId GROUP BY s.id")
    int getPlacesRestantes(int sessionId);

    @Query("SELECT * FROM session WHERE formationId = :formationId ORDER BY dateDebut ASC")
    List<Session> getSessionsByFormationSync(int formationId);
}