package com.odiousPanda.rainbowKt.model.dataSource.unsplash

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Exif {
    @SerializedName("make")
    @Expose
    var make: String? = null

    @SerializedName("model")
    @Expose
    var model: String? = null

    @SerializedName("exposure_time")
    @Expose
    var exposureTime: String? = null

    @SerializedName("aperture")
    @Expose
    var aperture: String? = null

    @SerializedName("focal_length")
    @Expose
    var focalLength: String? = null

    @SerializedName("iso")
    @Expose
    var iso: Int? = null
}