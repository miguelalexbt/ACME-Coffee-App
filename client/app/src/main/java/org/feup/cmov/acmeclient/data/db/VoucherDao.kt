package org.feup.cmov.acmeclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import org.feup.cmov.acmeclient.data.model.Voucher

@Dao
interface VoucherDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertAll(vouchers: List<Voucher>)

}