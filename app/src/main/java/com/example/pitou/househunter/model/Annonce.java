package com.example.pitou.househunter.model;

import java.util.Date;

/**
 * Created by kaoutar on 20/11/2016.
 */

public class Annonce {

    private String titre;
    private String description;
    private String adresse;
    private String telContact;
    private String prixLogment;
    private String datePublication;
    private String disponibiliteLogement;


    public Annonce(){

    }

    public Annonce(String titre, String disponibiliteLogement, String datePublication, String prixLogment, String telContact, String adresse, String description) {
        this.titre = titre;
        this.disponibiliteLogement = disponibiliteLogement;
        this.datePublication = datePublication;
        this.prixLogment = prixLogment;
        this.telContact = telContact;
        this.adresse = adresse;
        this.description = description;
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

    public String getTelContact() {
        return telContact;
    }

    public void setTelContact(String telContact) {
        this.telContact = telContact;
    }

    public String getPrixLogment() {
        return prixLogment;
    }

    public void setPrixLogment(String prixLogment) {
        this.prixLogment = prixLogment;
    }

    public String getDisponibiliteLogement() {
        return disponibiliteLogement;
    }

    public void setDisponibiliteLogement(String disponibiliteLogement) {
        this.disponibiliteLogement = disponibiliteLogement;
    }

    public String getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(String datePublication) {
        this.datePublication = datePublication;
    }
}
