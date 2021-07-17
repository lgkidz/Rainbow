package com.odiousPanda.rainbowKt.model.dataSource.unsplash

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Unsplash {
    @SerializedName("id")
    @Expose
    var id: String = ""

    @SerializedName("created_at")
    @Expose
    var createdAt: String = ""

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String = ""

    @SerializedName("width")
    @Expose
    var width: Int = 0

    @SerializedName("height")
    @Expose
    var height: Int = 0

    @SerializedName("color")
    @Expose
    var color: String = ""

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("alt_description")
    @Expose
    var altDescription: String = ""

    @SerializedName("urls")
    @Expose
    var urls: Urls = Urls()

    @SerializedName("links")
    @Expose
    var links: Links = Links()

    @SerializedName("categories")
    @Expose
    var categories: List<Any>? = null

    @SerializedName("sponsored")
    @Expose
    var sponsored: Boolean = false

    @SerializedName("sponsored_by")
    @Expose
    var sponsoredBy: String = ""

    @SerializedName("sponsored_impressions_id")
    @Expose
    var sponsoredImpressionsId: String = ""

    @SerializedName("likes")
    @Expose
    var likes: Int = 0

    @SerializedName("liked_by_user")
    @Expose
    var likedByUser: Boolean = false

    @SerializedName("current_user_collections")
    @Expose
    var currentUserCollections: List<Any>? = null

    @SerializedName("user")
    @Expose
    var user: User = User()

    @SerializedName("exif")
    @Expose
    var exif: Exif = Exif()

    @SerializedName("views")
    @Expose
    var views: Int = 0

    @SerializedName("downloads")
    @Expose
    var downloads: Int = 0
}
