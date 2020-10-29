package org.feup.cmov.acmeclient.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.model.User

@Database(entities = [User::class, Item::class], exportSchema = false, version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
}