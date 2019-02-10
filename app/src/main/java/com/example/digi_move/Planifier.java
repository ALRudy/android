package com.example.digi_move;

public class Planifier {
    private String planificateur;
    private String depart;
    private String destination;
    private String date;
    private String heure;
    private String classe;
    private String description;

    public Planifier(String planificateur, String depart, String destination, String date, String heure, String classe, String description) {
        this.planificateur = planificateur;
        this.depart = depart;
        this.destination = destination;
        this.date = date;
        this.heure = heure;
        this.classe = classe;
        this.description = description;
    }

    public Planifier(){}

    public String getPlanificateur() {
        return planificateur;
    }

    public void setPlanificateur(String planificateur) {
        this.planificateur = planificateur;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
