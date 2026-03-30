package com.openminds.app.database.entity;

public class FormationTop {
    private int formationId;
    private String titre;
    private int nbInscrits;
    public FormationTop() {}

    public int getFormationId() {
        return formationId;
    }

    public String getTitre() {
        return titre;
    }

    public int getNbInscrits() {
        return nbInscrits;
    }

    public void setNbInscrits(int nbInscrits) {
        this.nbInscrits = nbInscrits;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setFormationId(int formationId) {
        this.formationId = formationId;
    }
}
