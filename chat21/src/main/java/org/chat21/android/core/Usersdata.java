package org.chat21.android.core;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Usersdata implements Serializable {
    @SerializedName("id")
    private String id = "";
    @SerializedName("name")
    private String name = "";
    @SerializedName("fname")
    private String fname = "";
    @SerializedName("lname")
    private String lname = "";
    @SerializedName("email")
    private String email = "";
    @SerializedName("country_code")
    private String country_code = "";
    @SerializedName("phone")
    private String phone = "";
    @SerializedName("church")
    private String church = "";
    @SerializedName("country")
    private String country = "";
    @SerializedName("city")
    private String city = "";
    @SerializedName("latitude")
    private String latitude = "";
    @SerializedName("longitude")
    private String longitude = "";
    @SerializedName("is_verify")
    private String is_verify = "";
    @SerializedName("otp_code")
    private String otp_code = "";
    @SerializedName("bio")
    private String bio = "";
    @SerializedName("gender")
    private String gender = "";
    @SerializedName("dob")
    private String dob = "";
    @SerializedName("isnotification")
    private String isnotification = "";
    @SerializedName("profile_pic")
    private String profile_pic = "";

    @SerializedName("is_friend")
    private String is_friend = "";

    @SerializedName("friend_type")
    private String friend_type = "";

    @SerializedName("isfriend")
    private boolean isfriend = false;

    @SerializedName("message")
    private MessageList mMessageList = null;

    @SerializedName("interest1")
    private String interest1 = "";

    @SerializedName("interest2")
    private String interest2 = "";

    @SerializedName("interest3")
    private String interest3 = "";

    @SerializedName("interest4")
    private String interest4 = "";

    @SerializedName("interest5")
    private String interest5 = "";

    @SerializedName("distance")
    private String distance = "";

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getInterest1() {
        return interest1;
    }

    public void setInterest1(String interest1) {
        this.interest1 = interest1;
    }

    public String getInterest2() {
        return interest2;
    }

    public void setInterest2(String interest2) {
        this.interest2 = interest2;
    }

    public String getInterest3() {
        return interest3;
    }

    public void setInterest3(String interest3) {
        this.interest3 = interest3;
    }

    public String getInterest4() {
        return interest4;
    }

    public void setInterest4(String interest4) {
        this.interest4 = interest4;
    }

    public String getInterest5() {
        return interest5;
    }

    public void setInterest5(String interest5) {
        this.interest5 = interest5;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MessageList getmMessageList() {
        return mMessageList;
    }

    public void setmMessageList(MessageList mMessageList) {
        this.mMessageList = mMessageList;
    }

    public boolean isIsfriend() {
        return isfriend;
    }

    public void setIsfriend(boolean isfriend) {
        this.isfriend = isfriend;
    }

    public String getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(String is_friend) {
        this.is_friend = is_friend;
    }

    public String getFriend_type() {
        return friend_type;
    }

    public void setFriend_type(String friend_type) {
        this.friend_type = friend_type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

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

