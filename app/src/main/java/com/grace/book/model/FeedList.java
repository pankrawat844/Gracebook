package com.grace.book.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedList implements Serializable {
    @SerializedName("id")
    private String id = "";
    @SerializedName("user_id")
    private String user_id = "";
    @SerializedName("details")
    private String details = "";
    @SerializedName("post_type")
    private String post_type = "";
    @SerializedName("is_user")
    private String is_user = "";
    @SerializedName("like_count")
    private String like_count = "";
    @SerializedName("comment_count")
    private String comment_count = "";
    @SerializedName("post_time")
    private String post_time = "";
    @SerializedName("post_path")
    private String post_path = "";
    @SerializedName("is_like")
    private boolean is_like = false;

    @SerializedName("users")
    private Usersdata mUsersdata=null;

    public String getId() {
        return id;
    }

    public boolean isIs_like() {
        return is_like;
    }

    public void setIs_like(boolean is_like) {
        this.is_like = is_like;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getIs_user() {
        return is_user;
    }

    public void setIs_user(String is_user) {
        this.is_user = is_user;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getPost_path() {
        return post_path;
    }

    public void setPost_path(String post_path) {
        this.post_path = post_path;
    }

    public Usersdata getmUsersdata() {
        return mUsersdata;
    }

    public void setmUsersdata(Usersdata mUsersdata) {
        this.mUsersdata = mUsersdata;
    }
}
