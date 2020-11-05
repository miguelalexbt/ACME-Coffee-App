package org.feup.cmov.acmeterminal.data.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ValidateVoucherRequest(
    @SerializedName("order")
    @Expose
    val order: String
)
