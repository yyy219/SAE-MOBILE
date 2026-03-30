package com.openminds.app.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.openminds.app.database.dao.ContenuDao;
import com.openminds.app.database.dao.FormationDao;
import com.openminds.app.database.dao.InscriptionDao;
import com.openminds.app.database.dao.ResultatQuizDao;
import com.openminds.app.database.dao.SessionDao;
import com.openminds.app.database.dao.StatistiquesDao;
import com.openminds.app.database.dao.TelechargementDao;
import com.openminds.app.database.dao.UtilisateurDao;
import com.openminds.app.database.entity.Contenu;
import com.openminds.app.database.entity.Formation;
import com.openminds.app.database.entity.Inscription;
import com.openminds.app.database.entity.ResultatQuiz;
import com.openminds.app.database.entity.Session;
import com.openminds.app.database.entity.Telechargement;
import com.openminds.app.database.entity.Utilisateur;

import java.util.concurrent.Executors;

@Database(
        entities = {
                Formation.class,
                Session.class,
                Utilisateur.class,
                Inscription.class,
                Contenu.class,
                Telechargement.class,
                ResultatQuiz.class
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract FormationDao formationDao();
    public abstract SessionDao sessionDao();
    public abstract UtilisateurDao utilisateurDao();
    public abstract InscriptionDao inscriptionDao();
    public abstract ContenuDao contenuDao();
    public abstract TelechargementDao telechargementDao();
    public abstract ResultatQuizDao resultatQuizDao();
    public abstract StatistiquesDao statistiquesDao();

    // Callback : insérer le compte admin par défaut au premier lancement
    private static final RoomDatabase.Callback CALLBACK = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Exécuté UNE SEULE FOIS à la création de la base
            Executors.newSingleThreadExecutor().execute(() -> {
                if (INSTANCE != null) {
                    UtilisateurDao dao = INSTANCE.utilisateurDao();

                    // Compte admin par défaut
                    Utilisateur admin = new Utilisateur();
                    admin.setNom("Admin");
                    admin.setPrenom("OpenMinds");
                    admin.setEmail("admin@openminds.fr");
                    admin.setMotDePasse("admin123");
                    admin.setRole("admin");
                    admin.setDateInscription(new java.util.Date().toString());
                    dao.insert(admin);

                    // Compte bénévole de test
                    Utilisateur benevole = new Utilisateur();
                    benevole.setNom("Bénévole");
                    benevole.setPrenom("Test");
                    benevole.setEmail("benevole@openminds.fr");
                    benevole.setMotDePasse("test123");
                    benevole.setRole("benevole");
                    benevole.setDateInscription(new java.util.Date().toString());
                    dao.insert(benevole);
                }
            });
        }
    };

    // Singleton
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "openminds_db"
                            )
                            .fallbackToDestructiveMigration(true)
                            .addCallback(CALLBACK)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}