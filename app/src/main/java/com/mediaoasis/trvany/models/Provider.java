package com.mediaoasis.trvany.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nasr on 2/8/2017.
 */

public class Provider implements Parcelable {
    public static final Creator<Provider> CREATOR = new Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };
    private String BrokerID, Email, Address, Image, Name, Phone, Title, Country, Language;
    private int Activated, NumberOfRates, AllowNotifications;
    private float Rate;

    public Provider() {

    }

    protected Provider(Parcel in) {
        BrokerID = in.readString();
        Email = in.readString();
        Address = in.readString();
        Image = in.readString();
        Name = in.readString();
        Phone = in.readString();
        Title = in.readString();
        Country = in.readString();
        Language = in.readString();
        Activated = in.readInt();
        NumberOfRates = in.readInt();
        AllowNotifications = in.readInt();
        Rate = in.readFloat();
    }

    public String getBrokerID() {
        return BrokerID;
    }

    public void setBrokerID(String brokerID) {
        BrokerID = brokerID;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public int getActivated() {
        return Activated;
    }

    public void setActivated(int activated) {
        Activated = activated;
    }

    public int getNumberOfRates() {
        return NumberOfRates;
    }

    public void setNumberOfRates(int rate) {
        NumberOfRates = rate;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getAllowNotifications() {
        return AllowNotifications;
    }

    public void setAllowNotifications(int allowNotifications) {
        AllowNotifications = allowNotifications;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public float getRate() {
        return Rate;
    }

    public void setRate(float rate) {
        Rate = rate;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(BrokerID);
        parcel.writeString(Email);
        parcel.writeString(Address);
        parcel.writeString(Image);
        parcel.writeString(Name);
        parcel.writeString(Phone);
        parcel.writeString(Title);
        parcel.writeString(Country);
        parcel.writeString(Language);
        parcel.writeInt(Activated);
        parcel.writeInt(NumberOfRates);
        parcel.writeInt(AllowNotifications);
        parcel.writeFloat(Rate);
    }
}
