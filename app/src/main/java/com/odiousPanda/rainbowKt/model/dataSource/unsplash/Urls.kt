package com.odiousPanda.rainbowKt.model.dataSource.unsplash

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Urls {
    @SerializedName("raw")
    @Expose
    var raw: String = ""

    @SerializedName("full")
    @Expose
    var full: String = ""

    @SerializedName("regular")
    @Expose
    var regular: String = ""

    @SerializedName("small")
    @Expose
    var small: String = ""

    @SerializedName("thumb")
    @Expose
    var thumb: String = ""
}