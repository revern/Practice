package com.googlemaps.template.myapplication.model;

/**
 * Created by Алмаз on 17.09.2015.
 */
public class Locality {
    String locName;
    double lat;
    double lng;

    public Locality(String name, Double lat, Double lng){
        locName=name;
        this.lat=lat;
        this.lng=lng;
    }
    public Locality(){

    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
