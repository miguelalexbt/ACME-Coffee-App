package org.feup.cmov.acmeclient.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "voucher", indices = [Index(value = ["id", "userId"], unique = true)])
data class Voucher(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @Expose
    val id: String = "",

    // TODO foreign key??
    @ColumnInfo(name = "userId")
    @SerializedName("userId")
    @Expose
    val userId: String = "",

    @ColumnInfo(name = "type")
    @SerializedName("type")
    @Expose
    val type: Char = ' '
)