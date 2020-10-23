package org.feup.cmov.acmeclient

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.feup.cmov.acmeclient.data.WebService
import org.feup.cmov.acmeclient.data.db.AppDatabase
import org.feup.cmov.acmeclient.data.db.UserDao
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(app, AppDatabase::class.java, "db").build()

    @Provides
    fun provideWebService(): WebService {
        return Retrofit.Builder()
//            .baseUrl("http://10.0.2.2")
            .baseUrl("http://192.168.1.71") // Miguel
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}