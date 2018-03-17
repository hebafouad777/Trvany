package com.mediaoasis.trvany.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nasr on 2/21/2017.
 */

public class Order implements Parcelable {
    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
    private String OrderID, UserID, UserName, UserPhone, UserImage, PropertyID, PropertyImage, PropertyTitle,
            BrokerID, BrokerName, BrokerImage, BrokerTitle, BrokerPhone,
            Time, Date, Status, Fees, PickupName, PickupAddress, PickupSign;
    private double PickupLatitude, PickupLongitude;
    private boolean isUserReminder, isBrokerReminder;

    public Order() {

    }

    protected Order(Parcel in) {
        OrderID = in.readString();
        UserID = in.readString();
        UserName = in.readString();
        UserPhone = in.readString();
        UserImage = in.readString();
        PropertyID = in.readString();
        PropertyImage = in.readString();
        PropertyTitle = in.readString();
        BrokerID = in.readString();
        BrokerName = in.readString();
        BrokerImage = in.readString();
        BrokerTitle = in.readString();
        BrokerPhone = in.readString();
        Time = in.readString();
        Date = in.readString();
        Status = in.readString();
        Fees = in.readString();
        PickupName = in.readString();
        PickupAddress = in.readString();
        PickupSign = in.readString();
        PickupLatitude = in.readDouble();
        PickupLongitude = in.readDouble();
        isUserReminder = in.readByte() != 0;
        isBrokerReminder = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(OrderID);
        parcel.writeString(UserID);
        parcel.writeString(UserName);
        parcel.writeString(UserPhone);
        parcel.writeString(UserImage);
        parcel.writeString(PropertyID);
        parcel.writeString(PropertyImage);
        parcel.writeString(PropertyTitle);
        parcel.writeString(BrokerID);
        parcel.writeString(BrokerName);
        parcel.writeString(BrokerImage);
        parcel.writeString(BrokerTitle);
        parcel.writeString(BrokerPhone);
        parcel.writeString(Time);
        parcel.writeString(Date);
        parcel.writeString(Status);
        parcel.writeString(Fees);
        parcel.writeString(PickupName);
        parcel.writeString(PickupAddress);
        parcel.writeString(PickupSign);
        parcel.writeDouble(PickupLatitude);
        parcel.writeDouble(PickupLongitude);
        parcel.writeByte((byte) (isUserReminder ? 1 : 0));
        parcel.writeByte((byte) (isBrokerReminder ? 1 : 0));
    }

    public boolean isUserReminder() {
        return isUserReminder;
    }

    public void setUserReminder(boolean userReminder) {
        isUserReminder = userReminder;
    }

    public double getPickupLongitude() {
        return PickupLongitude;
    }

    public void setPickupLongitude(double pickupLongitude) {
        PickupLongitude = pickupLongitude;
    }

    public double getPickupLatitude() {
        return PickupLatitude;
    }

    public void setPickupLatitude(double pickupLatitude) {
        PickupLatitude = pickupLatitude;
    }

    public String getPickupSign() {
        return PickupSign;
    }

    public void setPickupSign(String pickupSign) {
        PickupSign = pickupSign;
    }

    public String getPickupAddress() {
        return PickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        PickupAddress = pickupAddress;
    }

    public String getPickupName() {
        return PickupName;
    }

    public void setPickupName(String pickupName) {
        PickupName = pickupName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getBrokerID() {
        return BrokerID;
    }

    public void setBrokerID(String brokerID) {
        BrokerID = brokerID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getBrokerName() {
        return BrokerName;
    }

    public void setBrokerName(String brokerName) {
        BrokerName = brokerName;
    }

    public String getBrokerImage() {
        return BrokerImage;
    }

    public void setBrokerImage(String brokerImage) {
        BrokerImage = brokerImage;
    }

    public String getBrokerTitle() {
        return BrokerTitle;
    }

    public void setBrokerTitle(String brokerTitle) {
        BrokerTitle = brokerTitle;
    }

    public boolean isBrokerReminder() {
        return isBrokerReminder;
    }

    public void setBrokerReminder(boolean brokerReminder) {
        isBrokerReminder = brokerReminder;
    }

    public String getPropertyID() {
        return PropertyID;
    }

    public void setPropertyID(String propertyID) {
        PropertyID = propertyID;
    }

//    public String getPropertyDescription() {
//        return PropertyDescription;
//    }

//    public void setPropertyDescription(String propertyDescription) {
//        PropertyDescription = propertyDescription;
//    }

    public String getPropertyImage() {
        return PropertyImage;
    }

    public void setPropertyImage(String propertyImage) {
        PropertyImage = propertyImage;
    }

    public String getPropertyTitle() {
        return PropertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        PropertyTitle = propertyTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getFees() {
        return Fees;
    }

    public void setFees(String fees) {
        Fees = fees;
    }

    public String getBrokerPhone() {
        return BrokerPhone;
    }

    public void setBrokerPhone(String brokerPhone) {
        BrokerPhone = brokerPhone;
    }

}
