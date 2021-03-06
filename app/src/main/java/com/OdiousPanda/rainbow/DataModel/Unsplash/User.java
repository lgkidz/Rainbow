package com.OdiousPanda.rainbow.DataModel.Unsplash;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("twitter_username")
    @Expose
    public Object twitterUsername;
    @SerializedName("portfolio_url")
    @Expose
    public String portfolioUrl;
    @SerializedName("bio")
    @Expose
    public String bio;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("links")
    @Expose
    public Links_ links;
    @SerializedName("profile_image")
    @Expose
    public ProfileImage profileImage;
    @SerializedName("instagram_username")
    @Expose
    public String instagramUsername;
    @SerializedName("total_collections")
    @Expose
    public Integer totalCollections;
    @SerializedName("total_likes")
    @Expose
    public Integer totalLikes;
    @SerializedName("total_photos")
    @Expose
    public Integer totalPhotos;
    @SerializedName("accepted_tos")
    @Expose
    public Boolean acceptedTos;

}
