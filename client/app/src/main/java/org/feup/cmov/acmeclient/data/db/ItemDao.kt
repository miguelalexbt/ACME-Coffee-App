package org.feup.cmov.acmeclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.model.User

@Dao
interface ItemDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(items: List<Item>)

    @Query("SELECT * FROM item")
    fun getAll() : Flow<List<Item>>
}