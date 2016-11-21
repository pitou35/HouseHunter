package com.example.pitou.househunter.model;

/**
 * Created by kaoutar on 20/11/2016.
 */

public class Annonce {

    private String titre;
    private String description;
    private String adresse;


    public Annonce(){

    }

    public Annonce(String titre, String description, String adresse) {
        this.titre = titre;
        this.description = description;
        this.adresse = adresse;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
}
