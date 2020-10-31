package org.feup.cmov.acmeclient.data.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("userId")
    @Expose
    val userId: String
)