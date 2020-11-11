package org.feup.cmov.acmeterminal.data.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SubmitOrderResponse(
    @SerializedName("orderNr")
    @Expose
    val orderNr: Int
)