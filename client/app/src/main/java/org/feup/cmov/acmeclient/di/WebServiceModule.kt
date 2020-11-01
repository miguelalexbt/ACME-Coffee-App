package org.feup.cmov.acmeclient.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okio.Buffer
import org.feup.cmov.acmeclient.data.WebService
import org.feup.cmov.acmeclient.data.api.details.ApiResponseAdapterFactory
import org.feup.cmov.acmeclient.utils.Cache
import org.feup.cmov.acmeclient.utils.Crypto
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
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
                val cachedUser = runBlocking {
                    Cache.cachedUser.first()!!
                }

                // Full path
                val url = request.url().url()
                val fullPath = url.path + if (url.query != null) ("?" + url.query) else ""

                // Timestamp
                val timestamp = DateTimeFormatter
                    .ofPattern("EEE, dd MMM yyyy HH:mm:ss O", Locale.ENGLISH)
                    .format(ZonedDateTime.now(ZoneOffset.UTC))

                // Add data to buffer
                buffer.write(cachedUser.userId.toByteArray())
                    .write(fullPath.toByteArray())
                    .write(timestamp.toByteArray())

                request.body()?.writeTo(buffer)

                val signature = Crypto.sign(cachedUser.username, buffer)

                buffer.close()

                newRequest.addHeader("Date", timestamp)
                newRequest.addHeader("User-Signature", "${cachedUser.userId}:$signature")

                return@addInterceptor chain.proceed(newRequest.build())
            }

            chain.proceed(request)
        }

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2")
//            .baseUrl("http://192.168.1.71") // Miguel
//            .baseUrl("http://192.168.1.2") // Xavi
//            .baseUrl("http://172.28.144.1") // Xavi Porto
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                )
            )
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .client(httpClient.build())
            .build()
            .create(WebService::class.java)
    }
}