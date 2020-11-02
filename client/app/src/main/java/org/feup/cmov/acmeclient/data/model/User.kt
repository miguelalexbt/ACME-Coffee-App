package org.feup.cmov.acmeclient.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user", indices = [Index(value = ["id", "username"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = false)
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "nif")
    val nif: String,

    @ColumnInfo(name = "cc_number")
    val ccNumber: String,

    @ColumnInfo(name = "cc_cvv")
    val ccCVV: String,

    @ColumnInfo(name = "cc_expiration")
    val ccExpiration: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "password")
    val password: String
)
