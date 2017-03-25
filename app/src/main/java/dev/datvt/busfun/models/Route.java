package dev.datvt.busfun.models;

import java.io.Serializable;

/**
 * Created by datvt on 5/12/2016.
 */
public class Route implements Serializable {
    private Place placeStart = null;
    private Place placeEnd = null;
    private String busID = "";

    public Place getPlaceStart() {
        return placeStart;
    }

    public void setPlaceStart(Place placeStart) {
        this.placeStart = placeStart;
    }

    public Place getPlaceEnd() {
        return placeEnd;
    }

    public void setPlaceEnd(Place placeEnd) {
        this.placeEnd = placeEnd;
    }

    public String getBusID() {
        return busID;
    }

    public void setBusID(String busID) {
        this.busID = busID;
    }

    @Override
    public String toString() {
        return busID;
    }
}
