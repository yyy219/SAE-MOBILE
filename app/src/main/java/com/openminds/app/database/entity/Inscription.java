package com.openminds.app.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "inscription",
        indices = {
                @Index("utilisateurId"),
                @Index("sessionId"),
                @Index(value = {"utilisateurId", "sessionId"}, unique = true)
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Utilisateur.class,
                        parentColumns = "id",
                        childColumns = "utilisateurId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Session.class,
                        parentColumns = "id",
                        childColumns = "sessionId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Inscription {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int utilisateurId;
    private int sessionId;
    private String dateInscription;
    private String statut;
    private int progressionPourcentage;

    private long timestampInscription; // millisecondes — pour le filtrage par période

    // ── Getters ──────────────────────────────────────
    public int getId()                      { return id; }
    public int getUtilisateurId()           { return utilisateurId; }
    public int getSessionId()               { return sessionId; }
    public String getDateInscription()      { return dateInscription; }
    public String getStatut()               { return statut; }
    public int getProgressionPourcentage()  { return progressionPourcentage; }

    public long getTimestampInscription() { return timestampInscription; }

    // ── Setters ──────────────────────────────────────
    public void setId(int id)                              { this.id = id; }
    public void setUtilisateurId(int utilisateurId)        { this.utilisateurId = utilisateurId; }
    public void setSessionId(int sessionId)                { this.sessionId = sessionId; }
    public void setDateInscription(String date)            { this.dateInscription = date; }
    public void setStatut(String statut)                   { this.statut = statut; }
    public void setProgressionPourcentage(int progression) { this.progressionPourcentage = progression; }

    public void setTimestampInscription(long ts) { this.timestampInscription = ts; }

}