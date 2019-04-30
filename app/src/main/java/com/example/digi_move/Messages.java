package com.example.digi_move;

import android.os.Parcel;
import android.os.Parcelable;

public class Messages implements Parcelable {
    String id_env;
    String id_rec;
    String message;
    String date;
    String heure;
    String email;

    public Messages(String id_env, String id_rec, String message, String date, String heure, String email, Boolean lu) {
        this.id_env = id_env;
        this.id_rec = id_rec;
        this.message = message;
        this.date = date;
        this.heure = heure;
        this.email = email;
        this.lu = lu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    Boolean lu;
    public  Messages(){

    }
    @Override
    public String toString() {
        return "Messages{" +
                "id_env='" + id_env + '\'' +
                ", id_rec='" + id_rec + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", heure='" + heure + '\'' +
                ", lu=" + lu +
                '}';
    }

    public String getId_env() {
        return id_env;
    }

    public void setId_env(String id_env) {
        this.id_env = id_env;
    }

    public String getId_rec() {
        return id_rec;
    }

    public void setId_rec(String id_rec) {
        this.id_rec = id_rec;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Boolean getLu() {
        return lu;
    }

    public void setLu(Boolean lu) {
        this.lu = lu;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id_env);
        dest.writeString(this.id_rec);
        dest.writeString(this.message);
        dest.writeString(this.date);
        dest.writeString(this.heure);
        dest.writeValue(this.lu);
    }

    protected Messages(Parcel in) {
        this.id_env = in.readString();
        this.id_rec = in.readString();
        this.message = in.readString();
        this.date = in.readString();
        this.heure = in.readString();
        this.lu = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Messages> CREATOR = new Creator<Messages>() {
        @Override
        public Messages createFromParcel(Parcel source) {
            return new Messages(source);
        }

        @Override
        public Messages[] newArray(int size) {
            return new Messages[size];
        }
    };
}
