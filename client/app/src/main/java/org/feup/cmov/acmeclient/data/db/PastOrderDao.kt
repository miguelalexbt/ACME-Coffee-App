package org.feup.cmov.acmeclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.model.PastOrder

@Dao
interface PastOrderDao {
    @Insert(onConflict = REPLACE)
    fun insertAll(pastOrders: List<PastOrder>)

    @Query("SELECT * FROM pastOrder WHERE userId = :userId")
    fun getAll(userId: String): Flow<List<PastOrder>>
}