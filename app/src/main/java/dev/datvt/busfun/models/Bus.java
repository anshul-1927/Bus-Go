package dev.datvt.busfun.models;

import java.io.Serializable;

/**
 * Created by datvt on 5/2/2016.
 */
public class Bus implements Serializable {
    private String id;
    private String code;
    private String name;
    private String cost;
    private String time;
    private String frequency;
    private String direction_go;
    private String direction_back;
    private String goRoutePath;
    private String returnRoutePath;
    private String goRouteThroughStop;
    private String returnRouteThroughStop;


    public Bus() {

    }

    public Bus(String id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDirection_go() {
        return direction_go;
    }

    public void setDirection_go(String direction_go) {
        this.direction_go = direction_go;
    }

    public String getDirection_back() {
        return direction_back;
    }

    public void setDirection_back(String direction_back) {
        this.direction_back = direction_back;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGoRoutePath() {
        return goRoutePath;
    }

    public void setGoRoutePath(String goRoutePath) {
        this.goRoutePath = goRoutePath;
    }

    public String getReturnRoutePath() {
        return returnRoutePath;
    }

    public void setReturnRoutePath(String returnRoutePath) {
        this.returnRoutePath = returnRoutePath;
    }

    public String getGoRouteThroughStop() {
        return goRouteThroughStop;
    }

    public void setGoRouteThroughStop(String goRouteThroughStop) {
        this.goRouteThroughStop = goRouteThroughStop;
    }

    public String getReturnRouteThroughStop() {
        return returnRouteThroughStop;
    }

    public void setReturnRouteThroughStop(String returnRouteThroughStop) {
        this.returnRouteThroughStop = returnRouteThroughStop;
    }

    @Override
    public String toString() {
        return id + " - " + name + " - " + cost + " - " + time + " - " + frequency;
    }
}
