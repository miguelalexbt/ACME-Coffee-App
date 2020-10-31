package org.feup.cmov.acmeclient.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.time.Instant
import java.time.format.DateTimeFormatter

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = false)
    @Expose
    val id: String = "",

    @ColumnInfo(name = "type")
    @Expose
    val type: String? = null,

    @ColumnInfo(name = "name")
    @Expose
    val name: String? = null,

    @ColumnInfo(name = "price")
    @Expose
    val price: Double? = null,

    @ColumnInfo(name = "added_at")
    val addedAt: String? = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
)