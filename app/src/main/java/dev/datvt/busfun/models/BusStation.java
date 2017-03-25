package dev.datvt.busfun.models;

import java.io.Serializable;

/**
 * Created by datvt on 5/2/2016.
 */
public class BusStation implements Serializable {
    private String id;
    private String code;
    private String name;
    private String busPassList;
    private String location;

    public BusStation() {

    }

    public BusStation(String id, String code, String name, String busPassList, String location) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.busPassList = busPassList;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusPassList() {
        return busPassList;
    }

    public void setBusPassList(String busPassList) {
        this.busPassList = busPassList;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    @Override
    public String toString() {
        return name;
    }
}
