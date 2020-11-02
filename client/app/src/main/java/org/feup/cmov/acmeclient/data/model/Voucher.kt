package org.feup.cmov.acmeclient.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

// TODO add annotation for JSON when needed (if needed)

@Entity(tableName = "voucher")
data class Voucher(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",

    val type: Char = ' '
)