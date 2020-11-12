package org.feup.cmov.acmeclient.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "receipt", indices = [Index(value = ["id", "orderId"], unique = true)])
data class Receipt(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @Expose
    val id: String = "",

    // TODO foreign key??
    @ColumnInfo(name = "orderId")
    @SerializedName("orderId")
    @Expose
    val orderId: String = "",

    @ColumnInfo(name = "nif")
    @SerializedName("nif")
    @Expose
    val nif: String = "",

    @ColumnInfo(name = "ccNumber")
    @SerializedName("ccNumber")
    @Expose
    val ccNumber: String = ""
)