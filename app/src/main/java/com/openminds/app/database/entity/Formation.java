package com.openminds.app.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "formation")
public class Formation {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titre;
    private String description;
    private String thematique;
    private int dureeMinutes;
    private String dateCreation;

    // ── Getters ──────────────────────────────────────
    public int getId()               { return id; }
    public String getTitre()         { return titre; }
    public String getDescription()   { return description; }
    public String getThematique()    { return thematique; }
    public int getDureeMinutes()     { return dureeMinutes; }
    public String getDateCreation()  { return dateCreation; }

    // ── Setters ──────────────────────────────────────
    public void setId(int id)                      { this.id = id; }
    public void setTitre(String titre)             { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setThematique(String thematique)   { this.thematique = thematique; }
    public void setDureeMinutes(int duree)         { this.dureeMinutes = duree; }
    public void setDateCreation(String date)       { this.dateCreation = date; }
}