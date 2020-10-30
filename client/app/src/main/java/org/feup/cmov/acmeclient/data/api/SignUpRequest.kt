package org.feup.cmov.acmeclient.data.api

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("name")
    var name: String,

    @SerializedName("nif")
    var nif: String,

    @SerializedName("ccNumber")
    var ccNumber: String,

    @SerializedName("ccExpiration")
    var ccExpiration: String,

    @SerializedName("ccCVV")
    var ccCVV: String,

    @SerializedName("publicKey")
    var publicKey: String
)