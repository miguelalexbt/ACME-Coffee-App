package org.feup.cmov.acmeclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.model.Voucher

@Dao
interface VoucherDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertAll(vouchers: List<Voucher>)

    @Query("SELECT * FROM voucher")
    fun getAll() : Flow<List<Voucher>>

    @Query("SELECT * FROM voucher ORDER BY added_at DESC LIMIT 1")
    fun getLastAdded() : Flow<Voucher?>
}