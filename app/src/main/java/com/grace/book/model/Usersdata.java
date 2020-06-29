package com.grace.book.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Usersdata implements Serializable {
    @SerializedName("id")
    private String id="";
    @SerializedName("fname")
    private String fname="";
    @SerializedName("lname")
    private String lname="";
    @SerializedName("email")
    private String email="";
    @SerializedName("country_code")
    private String country_code="";
    @SerializedName("phone")
    private String phone="";
    @SerializedName("church")
    private String church="";
    @SerializedName("country")
    private String country="";
    @SerializedName("latitude")
    private String latitude="";
    @SerializedName("longitude")
    private String longitude="";
    @SerializedName("is_verify")
    private String is_verify="";
    @SerializedName("otp_code")
    private String otp_code="";
    @SerializedName("bio")
    private String bio="";
    @SerializedName("isnotification")
    private String isnotification="";
    @SerializedName("profile_pic")
    private String profile_pic="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getChurch() {
        return church;
    }

    public void setChurch(String church) {
        this.church = church;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIs_verify() {
        return is_verify;
    }

    public void setIs_verify(String is_verify) {
        this.is_verify = is_verify;
    }

    public String getOtp_code() {
        return otp_code;
    }

    public void setOtp_code(String otp_code) {
        this.otp_code = otp_code;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getIsnotification() {
        return isnotification;
    }

    public void setIsnotification(String isnotification) {
        this.isnotification = isnotification;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}

