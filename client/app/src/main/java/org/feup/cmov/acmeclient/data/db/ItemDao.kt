package org.feup.cmov.acmeclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.db.details.ItemUpdate
import org.feup.cmov.acmeclient.data.model.Item

@Dao
interface ItemDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertAll(items: List<Item>)

    @Query("SELECT * FROM item")
    fun getAll(): Flow<List<Item>>

    @Query("SELECT * FROM item ORDER BY addedAt DESC LIMIT 1")
    fun getLastAdded(): Flow<Item?>

    @Update(entity = Item::class)
    fun setFavoriteItems(update: ItemUpdate)
}