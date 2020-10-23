package org.feup.cmov.acmeclient.data.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.feup.cmov.acmeclient.data.model.User

data class SignUpRequest(
    @SerializedName("user")
    @Expose
    var user: User,

    @SerializedName("cert")
    @Expose
    var cert: String,

    @SerializedName("timestamp")
    @Expose
    var timestamp: Long = System.currentTimeMillis() / 1000
)