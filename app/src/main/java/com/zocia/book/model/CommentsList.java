package com.zocia.book.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentsList implements Serializable {

    @SerializedName("id")
    private String id="";
    @SerializedName("post_id")
    private String post_id="";
    @SerializedName("user_id")
    private String user_id="";
    @SerializedName("message")
    private String message="";
    @SerializedName("comment_time")
    private String comment_time="";
    @SerializedName("users")
    private Usersdata mUsersdata=null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public Usersdata getmUsersdata() {
        return mUsersdata;
    }

    public void setmUsersdata(Usersdata mUsersdata) {
        this.mUsersdata = mUsersdata;
    }
}
