package com.mediaoasis.trvany.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by Nasr on 1/31/2017.
 */

public class Furniture implements Parcelable {
    public static final Creator<Furniture> CREATOR = new Creator<Furniture>() {
        @Override
        public Furniture createFromParcel(Parcel in) {
            return new Furniture(in);
        }

        @Override
        public Furniture[] newArray(int size) {
            return new Furniture[size];
        }
    };
    private String PropertyID, Title, Type, Offer, Address, Area, BrokerID, Description, /*PriceRange,*/ Country, City, Image;
    private int Price;
    private double Longitude, Latitude;
    private HashMap<String, String> Images;
//    private ArrayList<String> Images;


    public Furniture() {

    }

    protected Furniture(Parcel in) {
        PropertyID = in.readString();
        Title = in.readString();
        Type = in.readString();
        Offer = in.readString();
        Address = in.readString();
        Area = in.readString();
        BrokerID = in.readString();
        Description = in.readString();
//        PriceRange = in.readString();
        Country = in.readString();
        City = in.readString();
        Image = in.readString();
        Price = in.readInt();
        Longitude = in.readDouble();
        Latitude = in.readDouble();
//        Images = in.read();
    }

    public String getPropertyID() {
        return PropertyID;
    }

    public void setPropertyID(String propertyID) {
        PropertyID = propertyID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getOffer() {
        return Offer;
    }

    public void setOffer(String offer) {
        Offer = offer;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getBrokerID() {
        return BrokerID;
    }

    public void setBrokerID(String brokerID) {
        BrokerID = brokerID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

//    public String getPriceRange() {
//        return PriceRange;
//    }
//
//    public void setPriceRange(String priceRange) {
//        PriceRange = priceRange;
//    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public void setCityName(String cityName) {
        City = cityName;
    }

    //    public ArrayList<String> getImages() {
//        return Images;
//    }
//
//    public void setImages(ArrayList<String> images) {
//        Images = images;
//    }
    public HashMap<String, String> getImages() {
        return Images;
    }

    public void setImages(HashMap<String, String> images) {
        Images = images;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(PropertyID);
        parcel.writeString(Title);
        parcel.writeString(Type);
        parcel.writeString(Offer);
        parcel.writeString(Address);
        parcel.writeString(Area);
        parcel.writeString(BrokerID);
        parcel.writeString(Description);
//        parcel.writeString(PriceRange);
        parcel.writeString(Country);
        parcel.writeString(City);
        parcel.writeString(Image);
        parcel.writeInt(Price);
        parcel.writeDouble(Longitude);
        parcel.writeDouble(Latitude);
    }

}
