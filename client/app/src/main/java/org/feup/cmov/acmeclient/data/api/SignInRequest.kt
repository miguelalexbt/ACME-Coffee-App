package org.feup.cmov.acmeclient.data.api

import com.google.gson.annotations.SerializedName

data class SignInRequest (
    @SerializedName("username")
    var username: String,

    @SerializedName("password")
    var password: String
)