package com.odiousPanda.rainbowKt.model.dataSource.unsplash

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileImage {
    @SerializedName("small")
    @Expose
    var small: String = ""

    @SerializedName("medium")
    @Expose
    var medium: String = ""

    @SerializedName("large")
    @Expose
    var large: String = ""
}