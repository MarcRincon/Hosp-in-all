package com.example.newentryclear;

public class DeviceManager {
    private String Nom_tablet;
    private String ID_tablet;
    private String App_status;
    private String Ultima_Accion;
    private String Last_check;
    private Integer Battery_lvl;
    private String Device_charger;

    public DeviceManager() {

    }


    public String getNom_tablet() {
        return Nom_tablet;
    }

    public void setNom_tablet(String nom_tablet) {
        this.Nom_tablet = nom_tablet;
    }



    public String getApp_status() {
        return App_status;
    }

    public void setApp_status(String app_status) {
        this.App_status = app_status;
    }

    public String getUltima_Accion() {
        return Ultima_Accion;
    }

    public void setUltima_Accion(String ultima_Accion) {
        this.Ultima_Accion = ultima_Accion;
    }

    public String getLast_check() {
        return Last_check;
    }

    public void setLast_check(String last_check) {
        this.Last_check = last_check;
    }

    public Integer getBattery_lvl() {
        return Battery_lvl;
    }

    public void setBattery_lvl(Integer battery_lvl) {
        Battery_lvl = battery_lvl;
    }

    public String getDevice_charger() {
        return Device_charger;
    }

    public void setDevice_charger(String device_charger) {
        Device_charger = device_charger;
    }

    public String getID_tablet() {
        return ID_tablet;
    }

    public void setID_tablet(String ID_tablet) {
        this.ID_tablet = ID_tablet;
    }
}
