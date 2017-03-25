package dev.datvt.busfun.models;

import java.io.Serializable;

/**
 * Created by datvt on 5/12/2016.
 */
public class RouteDetail implements Serializable {

    private String type = "";
    private String distance = "";
    private String id = "";
    private String info = "";
    private boolean icon;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIcon() {
        return icon;
    }

    public void setIcon(boolean icon) {
        this.icon = icon;
    }
}
