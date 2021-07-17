package com.odiousPanda.rainbowKt.model.dataSource.unsplash

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Links_ {
    @SerializedName("self")
    @Expose
    var self: String = ""

    @SerializedName("html")
    @Expose
    var html: String = ""

    @SerializedName("photos")
    @Expose
    var photos: String = ""

    @SerializedName("likes")
    @Expose
    var likes: String = ""

    @SerializedName("portfolio")
    @Expose
    var portfolio: String = ""

    @SerializedName("following")
    @Expose
    var following: String = ""

    @SerializedName("followers")
    @Expose
    var followers: String = ""
}