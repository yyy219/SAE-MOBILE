package com.openminds.app.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


//Besoin de Getter et de Setter car respect du principe d'encapsulation car attributs sonten private//


//Entity structure la BD ici on definis a quoi ressemble un contenu en BD //
@Entity(
        tableName = "contenu",
        indices = { @Index("formationId") },
        foreignKeys = @ForeignKey(
                entity = Formation.class,
                parentColumns = "id",
                childColumns = "formationId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Contenu {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int formationId;
    private String type; //texte video ou quizz//
    private String titre;
    private String contenuTexte;
    private String urlMedia;
    private int ordre;


    public int getId()               { return id; }
    public int getFormationId()      { return formationId; }
    public String getType()          { return type; }
    public String getTitre()         { return titre; }
    public String getContenuTexte()  { return contenuTexte; }
    public String getUrlMedia()      { return urlMedia; }
    public int getOrdre()            { return ordre; }


    public void setId(int id)                      { this.id = id; }
    public void setFormationId(int formationId)    { this.formationId = formationId; }
    public void setType(String type)               { this.type = type; }
    public void setTitre(String titre)             { this.titre = titre; }
    public void setContenuTexte(String texte)      { this.contenuTexte = texte; }
    public void setUrlMedia(String urlMedia)       { this.urlMedia = urlMedia; }
    public void setOrdre(int ordre)                { this.ordre = ordre; }
}