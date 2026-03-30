package com.openminds.app.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "telechargement",
        indices = {
                @Index("utilisateurId"),
                @Index("formationId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Utilisateur.class,
                        parentColumns = "id",
                        childColumns = "utilisateurId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Formation.class,
                        parentColumns = "id",
                        childColumns = "formationId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Telechargement {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int utilisateurId;
    private int formationId;
    private String dateTelecharge;

    // ── Getters ──────────────────────────────────────
    public int getId()                  { return id; }
    public int getUtilisateurId()       { return utilisateurId; }
    public int getFormationId()         { return formationId; }
    public String getDateTelecharge()   { return dateTelecharge; }

    // ── Setters ──────────────────────────────────────
    public void setId(int id)                        { this.id = id; }
    public void setUtilisateurId(int utilisateurId)  { this.utilisateurId = utilisateurId; }
    public void setFormationId(int formationId)      { this.formationId = formationId; }
    public void setDateTelecharge(String date)       { this.dateTelecharge = date; }
}