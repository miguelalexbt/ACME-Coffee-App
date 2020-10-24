package org.feup.cmov.acmeclient.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.feup.cmov.acmeclient.data.db.AppDatabase
import org.feup.cmov.acmeclient.data.db.UserDao
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(app, AppDatabase::class.java, "db").build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}