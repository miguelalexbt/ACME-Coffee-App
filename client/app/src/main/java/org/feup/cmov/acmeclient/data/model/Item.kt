package org.feup.cmov.acmeclient.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "item")
data class Item(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    var id: String,

    @SerializedName("type")
    var type: String,

    @SerializedName("name")
    var name: String,

    @SerializedName("price")
    var price: String
)