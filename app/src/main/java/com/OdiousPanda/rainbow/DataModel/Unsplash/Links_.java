package com.OdiousPanda.rainbow.DataModel.Unsplash;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links_ {

    @SerializedName("self")
    @Expose
    public String self;
    @SerializedName("html")
    @Expose
    public String html;
    @SerializedName("photos")
    @Expose
    public String photos;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("portfolio")
    @Expose
    public String portfolio;
    @SerializedName("following")
    @Expose
    public String following;
    @SerializedName("followers")
    @Expose
    public String followers;

}
