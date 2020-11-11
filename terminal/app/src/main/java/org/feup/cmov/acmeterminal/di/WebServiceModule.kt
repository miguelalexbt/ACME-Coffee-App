package org.feup.cmov.acmeterminal.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import org.feup.cmov.acmeterminal.data.WebService
import org.feup.cmov.acmeterminal.data.api.details.ApiResponseAdapterFactory
import org.feup.cmov.acmeterminal.utils.WEB_SERVICE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object WebServiceModule {

    @Singleton
    @Provides
    fun provideWebService(): WebService {
        return Retrofit.Builder()
            .baseUrl(WEB_SERVICE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                )
            )
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .build()
            .create(WebService::class.java)
    }

}