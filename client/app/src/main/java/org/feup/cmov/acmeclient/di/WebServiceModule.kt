package org.feup.cmov.acmeclient.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okio.Buffer
import org.feup.cmov.acmeclient.data.WebService
import org.feup.cmov.acmeclient.data.api.details.ApiResponseAdapterFactory
import org.feup.cmov.acmeclient.utils.Cache
import org.feup.cmov.acmeclient.utils.Crypto
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object WebServiceModule {

    @Singleton
    @Provides
    fun provideWebService(): WebService {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val request = chain.request()
            val authenticatedRequest = request.tag(Invocation::class.java)
                ?.method()
                ?.getAnnotation(WebService.Companion.AuthenticatedRequest::class.java)

            if (authenticatedRequest != null) {
                val newRequest = request.newBuilder()
                val buffer = Buffer()

                // Cached user
                val cachedUser = Cache.cachedUser!!

                when (request.method()) {
                    "GET" -> {
                        buffer.write(cachedUser.userId.toByteArray())
                    }
                    else -> {
                        request.body()!!.writeTo(buffer)
                    }
                }

                val signature = Crypto.sign(cachedUser.username, buffer)

                newRequest.addHeader(
                    "User-Signature",
                    "${cachedUser.userId}:$signature"
                )

                buffer.close()

                return@addInterceptor chain.proceed(newRequest.build())
            }

            chain.proceed(request)
        }

        return Retrofit.Builder()
//            .baseUrl("http://10.0.2.2")
            .baseUrl("http://192.168.1.71") // Miguel
//            .baseUrl("http://192.168.1.2") // Xavi
//            .baseUrl("http://172.28.144.1") // Xavi Porto
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .client(httpClient.build())
            .build()
            .create(WebService::class.java)
    }
}