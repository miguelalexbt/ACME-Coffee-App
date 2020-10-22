package org.feup.cmov.acmeclient.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("type")
    @Expose
    var type: String,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("price")
    @Expose
    var price: String
)