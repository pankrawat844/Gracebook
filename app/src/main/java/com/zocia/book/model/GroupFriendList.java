package com.zocia.book.model;

import com.google.gson.annotations.SerializedName;

public class GroupFriendList {
    @SerializedName("id")
    private String id="";
    @SerializedName("group_id")
    private String group_id="";
    @SerializedName("user_id")
    private String user_id="";
    @SerializedName("users")
    private Usersdata mUsersdata=null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Usersdata getmUsersdata() {
        return mUsersdata;
    }

    public void setmUsersdata(Usersdata mUsersdata) {
        this.mUsersdata = mUsersdata;
    }
}
