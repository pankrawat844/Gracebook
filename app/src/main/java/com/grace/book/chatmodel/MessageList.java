package com.grace.book.chatmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageList implements Serializable {
    @SerializedName("sender_id")
    public String sender_id;
    @SerializedName("receiver_id")
    public String receiver_id;
    @SerializedName("duration")
    public String duration;
    @SerializedName("type")
    public String type;
    @SerializedName("message")
    public String message;
    @SerializedName("imagepath")
    public String imagepath;
    @SerializedName("id")
    public String id;
    @SerializedName("notification_type")
    public String notificaiton_type;


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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
