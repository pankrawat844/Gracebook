package com.grace.book.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GroupList implements Serializable {
    @SerializedName("id")
    private String id="";
    @SerializedName("group_id")
    private String group_id="";
    @SerializedName("user_id")
    private String user_id="";
    @SerializedName("group_name")
    private String group_name="";
    @SerializedName("group_owner")
    private String group_owner="";

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

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_owner() {
        return group_owner;
    }

    public void setGroup_owner(String group_owner) {
        this.group_owner = group_owner;
    }
}
