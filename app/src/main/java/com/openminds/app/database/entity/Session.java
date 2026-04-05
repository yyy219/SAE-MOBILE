package com.openminds.app.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "session",
        indices = { @Index("formationId") },
        foreignKeys = @ForeignKey(
                entity = Formation.class,
                parentColumns = "id",
                childColumns = "formationId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Session {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int formationId;
    private String type;
    private String dateDebut;
    private String dateFin;
    private String lieu;
    private String lienOnline;
    private int placesMax;


    public int getId()            { return id; }
    public int getFormationId()   { return formationId; }
    public String getType()       { return type; }
    public String getDateDebut()  { return dateDebut; }
    public String getDateFin()    { return dateFin; }
    public String getLieu()       { return lieu; }
    public String getLienOnline() { return lienOnline; }
    public int getPlacesMax()     { return placesMax; }


    public void setId(int id)                   { this.id = id; }
    public void setFormationId(int formationId) { this.formationId = formationId; }
    public void setType(String type)            { this.type = type; }
    public void setDateDebut(String dateDebut)  { this.dateDebut = dateDebut; }
    public void setDateFin(String dateFin)      { this.dateFin = dateFin; }
    public void setLieu(String lieu)            { this.lieu = lieu; }
    public void setLienOnline(String lien)      { this.lienOnline = lien; }
    public void setPlacesMax(int placesMax)     { this.placesMax = placesMax; }
}