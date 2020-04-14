package com.example.newentryclear;

public class BatteryWarnings {
    private String warning_type;
    private String id_tablet;
    private String nom_tablet;
    private Integer battery_lvl;
    private String last_check;

    public BatteryWarnings() {

    }


    public String getNom_tablet() {
        return nom_tablet;
    }

    public void setNom_tablet(String nom_tablet) {
        this.nom_tablet = nom_tablet;
    }

    public Integer getBattery_lvl() {
        return battery_lvl;
    }

    public void setBattery_lvl(Integer battery_lvl) {
        this.battery_lvl = battery_lvl;
    }

    public String getLast_check() {
        return last_check;
    }

    public void setLast_check(String last_check) {
        this.last_check = last_check;
    }

    public String getWarning_type() {
        return warning_type;
    }

    public void setWarning_type(String warning_type) {
        this.warning_type = warning_type;
    }

    public String getId_tablet() {
        return id_tablet;
    }

    public void setId_tablet(String id_tablet) {
        this.id_tablet = id_tablet;
    }
}
