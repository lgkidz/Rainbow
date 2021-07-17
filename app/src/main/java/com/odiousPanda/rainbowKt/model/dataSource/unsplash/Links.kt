package com.odiousPanda.rainbowKt.model.dataSource.unsplash

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Links {
    @SerializedName("self")
    @Expose
    var self: String = ""

    @SerializedName("html")
    @Expose
    var html: String = ""

    @SerializedName("download")
    @Expose
    var download: String = ""

    @SerializedName("download_location")
    @Expose
    var downloadLocation: String = ""
}