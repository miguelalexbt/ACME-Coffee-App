package org.feup.cmov.acmeclient.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @SerializedName("id")
    @Expose
    @PrimaryKey
    var id: String = "",

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("username")
    @Expose
    var username: String,

    @SerializedName("password")
    @Expose
    var password: String
)
