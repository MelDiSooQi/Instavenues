package com.instavenues.model;

/**
 * Created by MelDiSooQi on 4/5/2017.
 */

public class Place {
    private String ID;
    private String place;

    public Place() {
    }

    public Place(String ID, String place) {
        this.ID = ID;
        this.place = place;
    }

    public String getID() {
        return ID;
    }

    public String getPlace() {
        return place;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
