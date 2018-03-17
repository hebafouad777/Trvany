package com.mediaoasis.trvany.models;

/**
 * Created by Nasr on 1/31/2017.
 */

public class City {
    private String CityID, Name, Country, NameAr, NameTr;
    private double Latitude, Longitude;

    public City() {

    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getNameAr() {
        return NameAr;
    }

    public void setNameAr(String nameAr) {
        NameAr = nameAr;
    }

    public String getNameTr() {
        return NameTr;
    }

    public void setNameTr(String nameTr) {
        NameTr = nameTr;
    }
}
