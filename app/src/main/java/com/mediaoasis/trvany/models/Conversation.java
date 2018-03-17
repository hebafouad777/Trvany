package com.mediaoasis.trvany.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nasr on 3/14/2017.
 */

public class Conversation implements Parcelable {
    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };
    private String ConversationID, UserID, BrokerID, UserName, BrokerName, UserImage, BrokerImage;

    public Conversation() {

    }

    protected Conversation(Parcel in) {
        ConversationID = in.readString();
        UserID = in.readString();
        BrokerID = in.readString();
        UserName = in.readString();
        BrokerName = in.readString();
        UserImage = in.readString();
        BrokerImage = in.readString();
    }

    public String getConversationID() {
        return ConversationID;
    }

    public void setConversationID(String conversationID) {
        ConversationID = conversationID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getBrokerID() {
        return BrokerID;
    }

    public void setBrokerID(String brokerID) {
        BrokerID = brokerID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getBrokerName() {
        return BrokerName;
    }

    public void setBrokerName(String brokerName) {
        BrokerName = brokerName;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getBrokerImage() {
        return BrokerImage;
    }

    public void setBrokerImage(String brokerImage) {
        BrokerImage = brokerImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ConversationID);
        parcel.writeString(UserID);
        parcel.writeString(BrokerID);
        parcel.writeString(UserName);
        parcel.writeString(BrokerName);
        parcel.writeString(UserImage);
        parcel.writeString(BrokerImage);
    }
}
