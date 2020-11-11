package org.feup.cmov.acmeclient.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.feup.cmov.acmeclient.data.model.*
import androidx.room.TypeConverters
import org.feup.cmov.acmeclient.utils.DataConverter

@Database(entities = [User::class, Item::class, Voucher::class, PastOrder::class, Receipt::class], exportSchema = false, version = 1)
@TypeConverters(DataConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
    abstract fun voucherDao(): VoucherDao
    abstract fun pastOrderDao(): PastOrderDao
    abstract fun receiptDao(): ReceiptDao
}