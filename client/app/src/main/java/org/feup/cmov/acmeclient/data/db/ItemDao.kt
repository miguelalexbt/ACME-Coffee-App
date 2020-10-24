package org.feup.cmov.acmeclient.data.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.model.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAll() : Flow<List<Item>>
}