package org.feup.cmov.acmeterminal.data.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.feup.cmov.acmeterminal.data.model.Item

data class SubmitOrderResponse(
    @SerializedName("number")
    @Expose
    val number: Int,

    @SerializedName("items")
    @Expose
    val items: List<Item>,

    @SerializedName("vouchers")
    @Expose
    val vouchers: List<Char>,

    @SerializedName("total")
    @Expose
    val total: Double
)