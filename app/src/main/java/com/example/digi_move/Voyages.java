package com.example.digi_move;

public class Voyages {

    private String organisateur;
    private String depart;
    private String destination;
    private String date;
    private String heure;
    private String classe;
    private String tarif;
    private String description;
    private String statut;
    private String modalité;

    public Voyages(String organisateur, String depart, String destination, String date, String heure, String classe, String tarif, String description, String statut, String modalité) {
        this.organisateur = organisateur;
        this.depart = depart;
        this.destination = destination;
        this.date = date;
        this.heure = heure;
        this.classe = classe;
        this.tarif = tarif;
        this.description = description;
        this.statut = statut;
        this.modalité = modalité;
    }

    public String getOrganisateur() {
        return organisateur;
    }

    public void setOrganisateur(String organisateur) {
        this.organisateur = organisateur;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getTarif() {
        return tarif;
    }

    public void setTarif(String tarif) {
        this.tarif = tarif;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getModalité() {
        return modalité;
    }

    public void setModalité(String modalité) {
        this.modalité = modalité;
    }

    public Voyages(String organisateur){

    }
    public Voyages (){

    }
}
