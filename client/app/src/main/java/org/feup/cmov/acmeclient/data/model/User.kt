package org.feup.cmov.acmeclient.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class User(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    var id: String,

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

    @SerializedName("username")
    var username: String,

    @SerializedName("password")
    var password: String
)
