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

    // Enregistre le score d'un quiz
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ResultatQuiz resultat);

    // Tous les résultats d'un utilisateur
    @Query("SELECT * FROM resultat_quiz WHERE utilisateurId = :uid")
    LiveData<List<ResultatQuiz>> getResultatsByUtilisateur(int uid);

    // Résultat d'un utilisateur sur un quiz précis
    @Query("SELECT * FROM resultat_quiz " +
            "WHERE utilisateurId = :uid AND contenuId = :cid LIMIT 1")
    ResultatQuiz getResultatByQuiz(int uid, int cid);

    // Taux de réussite d'un utilisateur en %
    // Ex : si 7/10 + 8/10 → (15*100/20) = 75%
    @Query("SELECT (SUM(scoreObtenu) * 100 / SUM(scoreMax)) " +
            "FROM resultat_quiz WHERE utilisateurId = :uid")
    int getTauxReussiteUtilisateur(int uid);

    // Taux de réussite global (pour les stats admin US17)
    @Query("SELECT (SUM(scoreObtenu) * 100 / SUM(scoreMax)) " +
            "FROM resultat_quiz")
    int getTauxReussiteGlobal();
}