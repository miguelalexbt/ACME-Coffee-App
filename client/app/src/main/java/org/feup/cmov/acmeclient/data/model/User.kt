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

    @ColumnInfo(name = "ccNumber")
    val ccNumber: String,

    @ColumnInfo(name = "ccCVV")
    val ccCVV: String,

    @ColumnInfo(name = "ccExpiration")
    val ccExpiration: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "password")
    val password: String
)
