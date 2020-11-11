package org.feup.cmov.acmeclient.data.model

import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "pastOrder", indices = [Index(value = ["id", "userId"], unique = true)])
data class PastOrder(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @Expose
    val id: String = "",

    // TODO foreign key??
    @ColumnInfo(name = "userId")
    @SerializedName("userId")
    @Expose
    val userId: String = "",

    @ColumnInfo(name = "createdAt")
    @SerializedName("createdAt")
    @Expose
    val createdAt: String = "",

    @ColumnInfo(name = "items")
    @SerializedName("items")
    @Expose
    val items: String = "{}",

    @ColumnInfo(name = "vouchers")
    @SerializedName("vouchers")
    @Expose
    val vouchers: String = "[]",

    @ColumnInfo(name = "total")
    @SerializedName("total")
    @Expose
    val total: Double = 0.0
)