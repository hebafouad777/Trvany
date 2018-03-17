package com.mediaoasis.trvany.models;

/**
 * Created by Nasr on 2/15/2017.
 */

public class Booking {
    private String BookingID, PickupName, PickupAddress, Date, Time, Sign;
    private double PickupLatitude, PickupLongitude;

    public Booking(){

    }

    public String getBookingID() {
        return BookingID;
    }

    public void setBookingID(String bookingID) {
        BookingID = bookingID;
    }

    public String getPickupName() {
        return PickupName;
    }

    public void setPickupName(String pickupName) {
        PickupName = pickupName;
    }

    public String getPickupAddress() {
        return PickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        PickupAddress = pickupAddress;
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

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public double getPickupLatitude() {
        return PickupLatitude;
    }

    public void setPickupLatitude(double pickupLatitude) {
        PickupLatitude = pickupLatitude;
    }

    public double getPickupLongitude() {
        return PickupLongitude;
    }

    public void setPickupLongitude(double pickupLongitude) {
        PickupLongitude = pickupLongitude;
    }
}
