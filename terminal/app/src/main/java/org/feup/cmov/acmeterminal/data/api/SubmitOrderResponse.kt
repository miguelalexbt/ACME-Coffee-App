package org.feup.cmov.acmeterminal.data.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SubmitOrderResponse(
    @SerializedName("number")
    @Expose
    val number: Int,

    @SerializedName("vouchers")
    @Expose
    val vouchers: List<Char>,

    @SerializedName("total")
    @Expose
    val total: Double
)