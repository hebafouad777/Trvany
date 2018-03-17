package com.mediaoasis.trvany.models;

/**
 * Created by Nasr on 2/4/2017.
 */

public class Country {
    private String CountryID, Name, Region, NameAr, NameTr;

    public Country() {

    }

    public String getCountryID() {
        return CountryID;
    }

    public void setCountryID(String countryID) {
        CountryID = countryID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
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
