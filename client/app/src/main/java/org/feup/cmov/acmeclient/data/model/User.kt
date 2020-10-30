package org.feup.cmov.acmeclient.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user", indices = [Index(value = ["id", "username"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val nif: String,
    val ccNumber: String,
    val ccExpiration: String,
    val ccCVV: String,
    val username: String,
    val password: String
)
