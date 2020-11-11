package org.feup.cmov.acmeclient.data.db.details

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class ItemUpdate(
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "usersFavorite")
    val usersFavorite: Set<String>
)