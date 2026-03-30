package com.openminds.app.database.entity;

public class StatThematique {

    private String thematique;
    private int nbInscrits;


    public StatThematique() {}


    public String getThematique() {
        return thematique;
    }

    public int getNbInscrits() {
        return nbInscrits;
    }

    public void setThematique(String thematique) {
        this.thematique = thematique;
    }

    public void setNbInscrits(int nbInscrits) {
        this.nbInscrits = nbInscrits;
    }
}
