package com.openminds.app.database;


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.openminds.app.database.dao.ContenuDao;
import com.openminds.app.database.dao.FormationDao;
import com.openminds.app.database.dao.InscriptionDao;
import com.openminds.app.database.dao.ResultatQuizDao;
import com.openminds.app.database.dao.SessionDao;
import com.openminds.app.database.dao.TelechargementDao;
import com.openminds.app.database.dao.UtilisateurDao;
import com.openminds.app.database.dao.StatistiquesDao;

import com.openminds.app.database.entity.Contenu;
import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Inscription;
import com.openminds.app.database.entity.ResultatQuiz;
import com.openminds.app.database.entity.Session;
import com.openminds.app.database.entity.Telechargement;
import com.openminds.app.database.entity.Utilisateur;


//AppDatabase classe centrale de Room , declaration de toutes les entites fournit acces au DAO//
//Utilisation pattern Singleton pour s'assurer une seule instance de la base existe et evite acces concurents//

@Database(
        entities = {
                Formation.class,
                Session.class,
                Utilisateur.class,
                Inscription.class,
                Contenu.class,
                Telechargement.class,
                ResultatQuiz.class      // ajouté pour les quiz
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // Un accès par table
    public abstract FormationDao formationDao();
    public abstract SessionDao sessionDao();
    public abstract UtilisateurDao utilisateurDao();
    public abstract InscriptionDao inscriptionDao();
    public abstract ContenuDao contenuDao();
    public abstract TelechargementDao telechargementDao();
    public abstract ResultatQuizDao resultatQuizDao();

    public abstract StatistiquesDao statistiquesDao();

    // Singleton — une seule instance de la base
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "openminds_db"
                            )
                            .fallbackToDestructiveMigration( true)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


