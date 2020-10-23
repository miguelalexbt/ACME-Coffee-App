package org.feup.cmov.acmeclient.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.feup.cmov.acmeclient.data.model.User

data class SignInRequest (
    @SerializedName("username")
    @Expose
    var username: String,

    @SerializedName("password")
    @Expose
    var password: String
)