package com.openminds.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.openminds.app.database.entity.ResultatQuiz;
import java.util.List;

@Dao
public interface ResultatQuizDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ResultatQuiz resultat);


    @Query("SELECT * FROM resultat_quiz WHERE utilisateurId = :uid")
    LiveData<List<ResultatQuiz>> getResultatsByUtilisateur(int uid);


    @Query("SELECT * FROM resultat_quiz " +
            "WHERE utilisateurId = :uid AND contenuId = :cid LIMIT 1")
    ResultatQuiz getResultatByQuiz(int uid, int cid);


    @Query("SELECT (SUM(scoreObtenu) * 100 / SUM(scoreMax)) " +
            "FROM resultat_quiz WHERE utilisateurId = :uid")
    int getTauxReussiteUtilisateur(int uid);


    @Query("SELECT (SUM(scoreObtenu) * 100 / SUM(scoreMax)) " +
            "FROM resultat_quiz")
    int getTauxReussiteGlobal();
}