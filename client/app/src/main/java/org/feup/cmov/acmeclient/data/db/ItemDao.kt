package org.feup.cmov.acmeclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.feup.cmov.acmeclient.data.model.Item

@Dao
interface ItemDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(item: Item)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(items: List<Item>)

    @Query("SELECT * FROM item")
    fun getAll() : Flow<List<Item>>
}