package org.feup.cmov.acmeclient.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.format.DateTimeFormatter

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @Expose
    val id: String = "",

    @ColumnInfo(name = "type")
    @SerializedName("type")
    @Expose
    val type: String? = null,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    val name: String? = null,

    @ColumnInfo(name = "price")
    @SerializedName("price")
    @Expose
    val price: Double? = null,

    @ColumnInfo(name = "added_at")
    val addedAt: String? = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
)