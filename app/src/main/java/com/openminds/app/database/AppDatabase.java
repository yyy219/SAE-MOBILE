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

    private static final RoomDatabase.Callback CALLBACK = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                if (INSTANCE != null) {
                    UtilisateurDao userDao = INSTANCE.utilisateurDao();
                    FormationDao formationDao = INSTANCE.formationDao();
                    ContenuDao contenuDao = INSTANCE.contenuDao();


                    Utilisateur admin = new Utilisateur();
                    admin.setNom("Admin");
                    admin.setPrenom("OpenMinds");
                    admin.setEmail("admin@openminds.fr");
                    admin.setMotDePasse("admin123");
                    admin.setRole("admin");
                    admin.setDateInscription(new java.util.Date().toString());
                    userDao.insert(admin);

                    Utilisateur benevole = new Utilisateur();
                    benevole.setNom("Bénévole");
                    benevole.setPrenom("Test");
                    benevole.setEmail("benevole@openminds.fr");
                    benevole.setMotDePasse("test123");
                    benevole.setRole("benevole");
                    benevole.setDateInscription(new java.util.Date().toString());
                    userDao.insert(benevole);


                    Formation f1 = new Formation();
                    f1.setTitre("Formation au numérique");
                    f1.setDescription("Apprendre les bases du numérique");
                    f1.setThematique("inclusion");
                    f1.setDureeMinutes(60);
                    f1.setDateCreation("01/04/2026");
                    long id1 = formationDao.insert(f1);

                    Contenu f1m1 = new Contenu();
                    f1m1.setFormationId((int) id1);
                    f1m1.setTitre("Introduction au numérique");
                    f1m1.setType("texte");
                    f1m1.setContenuTexte("Découvrez les bases de l'informatique et d'internet.");
                    f1m1.setOrdre(1);
                    contenuDao.insert(f1m1);

                    Contenu f1m2 = new Contenu();
                    f1m2.setFormationId((int) id1);
                    f1m2.setTitre("Utiliser un ordinateur");
                    f1m2.setType("texte");
                    f1m2.setContenuTexte("Apprenez à naviguer sur un système d'exploitation.");
                    f1m2.setOrdre(2);
                    contenuDao.insert(f1m2);

                    Contenu f1m3 = new Contenu();
                    f1m3.setFormationId((int) id1);
                    f1m3.setTitre("Internet et sécurité");
                    f1m3.setType("texte");
                    f1m3.setContenuTexte("Les bonnes pratiques pour naviguer en sécurité.");
                    f1m3.setOrdre(3);
                    contenuDao.insert(f1m3);


                    Formation f2 = new Formation();
                    f2.setTitre("Introduction à l'inclusion");
                    f2.setDescription("Comprendre et promouvoir l'inclusion sociale");
                    f2.setThematique("inclusion");
                    f2.setDureeMinutes(45);
                    f2.setDateCreation("01/04/2026");
                    long id2 = formationDao.insert(f2);

                    Contenu f2m1 = new Contenu();
                    f2m1.setFormationId((int) id2);
                    f2m1.setTitre("Qu'est-ce que l'inclusion ?");
                    f2m1.setType("texte");
                    f2m1.setContenuTexte("Définition et enjeux de l'inclusion sociale.");
                    f2m1.setOrdre(1);
                    contenuDao.insert(f2m1);

                    Contenu f2m2 = new Contenu();
                    f2m2.setFormationId((int) id2);
                    f2m2.setTitre("Agir au quotidien");
                    f2m2.setType("texte");
                    f2m2.setContenuTexte("Des actions concrètes pour favoriser l'inclusion.");
                    f2m2.setOrdre(2);
                    contenuDao.insert(f2m2);
                }
            });
        }
    };

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