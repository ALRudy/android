package com.example.digi_move;

import android.os.Parcel;
import android.os.Parcelable;

public class Users implements Parcelable {
    private String id="";
    private String email="";
    private String pseudo="";
    private String nom="";
    private String prenom="";
    private String adresse="";
    private int numero=-1;
    private String profile="";

    public Users() {
    }

    public Users(String id, String email, String pseudo, String nom, String prenom, String adresse, int numero, String profile) {
        this.id = id;
        this.email = email;
        this.pseudo = pseudo;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.numero = numero;
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean isFirstLog() {
        return firstLog;
    }

    public void setFirstLog(boolean firstLog) {
        this.firstLog = firstLog;
    }

    boolean firstLog = true;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.email);
        dest.writeString(this.pseudo);
        dest.writeString(this.nom);
        dest.writeString(this.prenom);
        dest.writeString(this.adresse);
        dest.writeInt(this.numero);
        dest.writeString(this.profile);
        dest.writeByte(this.firstLog ? (byte) 1 : (byte) 0);
    }

    protected Users(Parcel in) {
        this.id = in.readString();
        this.email = in.readString();
        this.pseudo = in.readString();
        this.nom = in.readString();
        this.prenom = in.readString();
        this.adresse = in.readString();
        this.numero = in.readInt();
        this.profile = in.readString();
        this.firstLog = in.readByte() != 0;
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel source) {
            return new Users(source);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };
}
