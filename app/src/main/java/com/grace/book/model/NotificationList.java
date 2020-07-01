package com.grace.book.model;

import com.google.gson.annotations.SerializedName;

public class NotificationList {
    @SerializedName("id")
    private String id="";
    @SerializedName("user_id")
    private String user_id="";
    @SerializedName("notification_type")
    private String notification_type="";
    @SerializedName("details")
    private String details="";
    @SerializedName("type_id")
    private String type_id="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }
}
