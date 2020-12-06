package org.chat21.android.core;

import com.google.gson.annotations.SerializedName;

public class FriendList {
    @SerializedName("id")
    private String id = "";
    @SerializedName("sender_id")
    private String sender_id = "";
    @SerializedName("receiver_id")
    private String receiver_id = "";
    @SerializedName("friend_type")
    private String friend_type = "";
    @SerializedName("userinfo")
    private Usersdata mUsersdata = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getFriend_type() {
        return friend_type;
    }

    public void setFriend_type(String friend_type) {
        this.friend_type = friend_type;
    }

    public Usersdata getmUsersdata() {
        return mUsersdata;
    }

    public void setmUsersdata(Usersdata mUsersdata) {
        this.mUsersdata = mUsersdata;
    }
}
