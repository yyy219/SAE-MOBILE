package com.openminds.app.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "utilisateur")
public class Utilisateur {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String role;
    private String dateInscription;


    public int getId()                  { return id; }
    public String getNom()              { return nom; }
    public String getPrenom()           { return prenom; }
    public String getEmail()            { return email; }
    public String getMotDePasse()       { return motDePasse; }
    public String getRole()             { return role; }
    public String getDateInscription()  { return dateInscription; }


    public void setId(int id)                          { this.id = id; }
    public void setNom(String nom)                     { this.nom = nom; }
    public void setPrenom(String prenom)               { this.prenom = prenom; }
    public void setEmail(String email)                 { this.email = email; }
    public void setMotDePasse(String motDePasse)       { this.motDePasse = motDePasse; }
    public void setRole(String role)                   { this.role = role; }
    public void setDateInscription(String date)        { this.dateInscription = date; }
}