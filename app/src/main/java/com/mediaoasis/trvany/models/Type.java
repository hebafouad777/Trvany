package com.mediaoasis.trvany.models;

/**
 * Created by Nasr on 9/14/2017.
 */

public class Type {
    private String TypeID, Name, NameAr, NameTr;

    public Type() {

    }

    public String getTypeID() {
        return TypeID;
    }

    public void setTypeID(String offerID) {
        TypeID = offerID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNameAR() {
        return NameAr;
    }

    public void setNameAR(String nameAR) {
        NameAr = nameAR;
    }

    public String getNameTR() {
        return NameTr;
    }

    public void setNameTR(String nameTR) {
        NameTr = nameTR;
    }
}
