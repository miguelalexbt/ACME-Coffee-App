package org.feup.cmov.acmeclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.model.Receipt

@Dao
interface ReceiptDao {
    @Insert(onConflict = REPLACE)
    fun insert(receipt: Receipt)

    @Query("SELECT * FROM receipt WHERE orderId = :orderId")
    fun get(orderId: String): Flow<Receipt>
}