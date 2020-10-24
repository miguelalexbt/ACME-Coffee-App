package org.feup.cmov.acmeclient.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class Item(
    @SerializedName("id")
    @Expose
    @PrimaryKey
    var id: String,

    @SerializedName("type")
    @Expose
    var type: String,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("price")
    @Expose
    var price: String
)