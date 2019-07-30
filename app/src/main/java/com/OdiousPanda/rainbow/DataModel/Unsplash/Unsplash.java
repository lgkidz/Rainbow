package com.OdiousPanda.rainbow.DataModel.Unsplash;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Unsplash {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("width")
    @Expose
    public Integer width;
    @SerializedName("height")
    @Expose
    public Integer height;
    @SerializedName("color")
    @Expose
    public String color;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("alt_description")
    @Expose
    public String altDescription;
    @SerializedName("urls")
    @Expose
    public Urls urls;
    @SerializedName("links")
    @Expose
    public Links links;
    @SerializedName("categories")
    @Expose
    public List<Object> categories = null;
    @SerializedName("sponsored")
    @Expose
    public Boolean sponsored;
    @SerializedName("sponsored_by")
    @Expose
    public Object sponsoredBy;
    @SerializedName("sponsored_impressions_id")
    @Expose
    public Object sponsoredImpressionsId;
    @SerializedName("likes")
    @Expose
    public Integer likes;
    @SerializedName("liked_by_user")
    @Expose
    public Boolean likedByUser;
    @SerializedName("current_user_collections")
    @Expose
    public List<Object> currentUserCollections = null;
    @SerializedName("user")
    @Expose
    public User user;
    @SerializedName("exif")
    @Expose
    public Exif exif;
    @SerializedName("views")
    @Expose
    public Integer views;
    @SerializedName("downloads")
    @Expose
    public Integer downloads;

}
