package org.feup.cmov.acmeclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.model.Voucher

@Dao
interface VoucherDao {
    @Insert(onConflict = REPLACE)
    fun insertAll(vouchers: List<Voucher>)

    @Query("SELECT * FROM voucher WHERE userId = :userId")
    fun getAll(userId: String): Flow<List<Voucher>>

    @Query("DELETE FROM voucher WHERE userId = :userId")
    fun deleteAll(userId: String)

    @Transaction
    fun deleteAndInsert(userId: String, vouchers: List<Voucher>) {
        deleteAll(userId)
        insertAll(vouchers)
    }
}