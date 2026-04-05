package com.openminds.app.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "resultat_quiz",
        indices = {
                @Index("utilisateurId"),
                @Index("contenuId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Utilisateur.class,
                        parentColumns = "id",
                        childColumns = "utilisateurId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Contenu.class,
                        parentColumns = "id",
                        childColumns = "contenuId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class ResultatQuiz {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int utilisateurId;
    private int contenuId;
    private int scoreObtenu;
    private int scoreMax;
    private String datePassage;

    private long timestampPassage;


    public int getId()               { return id; }
    public int getUtilisateurId()    { return utilisateurId; }
    public int getContenuId()        { return contenuId; }
    public int getScoreObtenu()      { return scoreObtenu; }
    public int getScoreMax()         { return scoreMax; }
    public String getDatePassage()   { return datePassage; }

    public long getTimestampPassage() { return timestampPassage; }


    public void setId(int id)                      { this.id = id; }
    public void setUtilisateurId(int uid)          { this.utilisateurId = uid; }
    public void setContenuId(int contenuId)        { this.contenuId = contenuId; }
    public void setScoreObtenu(int scoreObtenu)    { this.scoreObtenu = scoreObtenu; }
    public void setScoreMax(int scoreMax)          { this.scoreMax = scoreMax; }
    public void setDatePassage(String date)        { this.datePassage = date; }

    public void setTimestampPassage(long ts) { this.timestampPassage = ts; }
}