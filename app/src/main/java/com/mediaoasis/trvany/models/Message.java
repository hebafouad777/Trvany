package com.mediaoasis.trvany.models;

/**
 * Created by Nasr on 3/14/2017.
 */

public class Message {
    private String MsgID, Text, Type, SenderId;
    private int isFromBroker, isDeletedByUser, isDeletedByBroker;
    private double Latitude, Longitude;


    public Message() {

    }

    public String getMsgID() {
        return MsgID;
    }

    public void setMsgID(String msgID) {
        MsgID = msgID;
    }

    public int getIsFromBroker() {
        return isFromBroker;
    }

    public void setIsFromBroker(int isFromBroker) {
        this.isFromBroker = isFromBroker;
    }

    public int getIsDeletedByUser() {
        return isDeletedByUser;
    }

    public void setIsDeletedByUser(int isDeletedByUser) {
        this.isDeletedByUser = isDeletedByUser;
    }

    public int getIsDeletedByBroker() {
        return isDeletedByBroker;
    }

    public void setIsDeletedByBroker(int isDeletedByBroker) {
        this.isDeletedByBroker = isDeletedByBroker;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
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
}
