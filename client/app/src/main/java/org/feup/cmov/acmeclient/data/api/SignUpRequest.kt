package org.feup.cmov.acmeclient.data.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("nif")
    @Expose
    var nif: String,

    @SerializedName("ccNumber")
    @Expose
    var ccNumber: String,

    @SerializedName("ccCVV")
    @Expose
    var ccCVV: String,

    @SerializedName("ccExpiration")
    @Expose
    var ccExpiration: String,

    @SerializedName("publicKey")
    @Expose
    var publicKey: String
)