package com.example.newentryclear;

public class AlarmasMedic {
    private String Nom_tablet;
    private String ID_tablet;
    private String Tipo_Alarma;
    private String Nom_user;
    private String password_user;
    private String time;

    public AlarmasMedic() {

    }

    public String getNom_tablet() {
        return Nom_tablet;
    }

    public void setNom_tablet(String nom_tablet) {
        this.Nom_tablet = nom_tablet;
    }


    public String getTipo_Alarma() {
        return Tipo_Alarma;
    }

    public void setTipo_Alarma(String tipo_Alarma) {
        this.Tipo_Alarma = tipo_Alarma;
    }

    public String getNom_user() {
        return Nom_user;
    }

    public void setNom_user(String nom_user) {
        this.Nom_user = nom_user;
    }

    public String getPassword_user() {
        return password_user;
    }

    public void setPassword_user(String password_user) {
        this.password_user = password_user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getID_tablet() {
        return ID_tablet;
    }

    public void setID_tablet(String ID_tablet) {
        this.ID_tablet = ID_tablet;
    }
}
