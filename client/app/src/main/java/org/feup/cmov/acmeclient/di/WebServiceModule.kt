package org.feup.cmov.acmeclient.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import org.feup.cmov.acmeclient.data.WebService
import org.feup.cmov.acmeclient.utils.ApiResponseAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ApplicationComponent::class)
object WebServiceModule {

    @Provides
    fun provideWebService(): WebService {
        return Retrofit.Builder()
//            .baseUrl("http://10.0.2.2")
            .baseUrl("http://192.168.1.71") // Miguel
//            .baseUrl("http://192.168.1.2") // Xavi
//            .baseUrl("http://172.28.144.1") // Xavi Porto
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .build()
            .create(WebService::class.java)
    }
}