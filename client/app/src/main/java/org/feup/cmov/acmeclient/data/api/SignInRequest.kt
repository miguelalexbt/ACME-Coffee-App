package org.feup.cmov.acmeclient.data.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SignInRequest (
    @SerializedName("username")
    @Expose
    var username: String,

    @SerializedName("password")
    @Expose
    var password: String
)