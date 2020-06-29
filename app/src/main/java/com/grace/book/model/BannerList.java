package com.grace.book.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BannerList implements Serializable {
    @SerializedName("id")
    private String id="";
    @SerializedName("title")
    private String title="";
    @SerializedName("description")
    private String description="";
    @SerializedName("web_link")
    private String web_link="";
    @SerializedName("image")
    private String image="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeb_link() {
        return web_link;
    }

    public void setWeb_link(String web_link) {
        this.web_link = web_link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

