package org.feup.cmov.acmeterminal.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("type")
    @Expose
    val type: String = "",

    @SerializedName("name")
    @Expose
    val name: String = "",

    @SerializedName("price")
    @Expose
    val price: Double = 0.0,

    @SerializedName("quantity")
    @Expose
    val quantity: Int = 0
)