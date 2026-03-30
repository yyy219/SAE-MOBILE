package com.openminds.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.openminds.app.database.entity.Utilisateur;
import java.util.List;

@Dao
public interface UtilisateurDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Utilisateur utilisateur);

    @Update
    void update(Utilisateur utilisateur);

    @Delete
    void delete(Utilisateur utilisateur);

    // Utilisé pour la page Login : cherche par email + mot de passe
    @Query("SELECT * FROM utilisateur WHERE email = :email " +
            "AND motDePasse = :motDePasse LIMIT 1")
    Utilisateur login(String email, String motDePasse);

    // Vérifie si un email est déjà utilisé lors de l'inscription
    @Query("SELECT COUNT(*) FROM utilisateur WHERE email = :email")
    int emailExiste(String email);

    @Query("SELECT * FROM utilisateur ORDER BY nom ASC")
    LiveData<List<Utilisateur>> getAllUtilisateurs();

    @Query("SELECT * FROM utilisateur WHERE role = 'benevole'")
    LiveData<List<Utilisateur>> getBenevoles();

    @Query("SELECT * FROM utilisateur WHERE id = :id")
    Utilisateur getById(int id);

    // FIX #8 : version LiveData pour observer depuis l'UI (MonEspaceActivity)
    @Query("SELECT * FROM utilisateur WHERE id = :id")
    LiveData<Utilisateur> getUtilisateurById(int id);
}