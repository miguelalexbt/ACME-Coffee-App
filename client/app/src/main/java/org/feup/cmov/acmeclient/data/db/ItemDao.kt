package org.feup.cmov.acmeclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.model.Item

@Dao
interface ItemDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertAll(items: List<Item>)

    @Query("SELECT * FROM item")
    fun getAll() : Flow<List<Item>>

    @Query("SELECT * FROM item ORDER BY added_at DESC LIMIT 1")
    fun getLastAddedItem() : Flow<Item?>
}