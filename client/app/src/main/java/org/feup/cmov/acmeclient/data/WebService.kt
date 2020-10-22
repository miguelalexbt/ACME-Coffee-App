package org.feup.cmov.acmeclient.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import okhttp3.ResponseBody
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.model.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.security.cert.Certificate

interface WebService {

    // Auth
    @POST("auth")
    fun signIn(
        @Field("username") username: String,
        @Field("password") password: String
    ): User

    @FormUrlEncoded
    @PUT("auth")
    suspend fun signUp(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("certificate") certificate: String
    ) : User

    //Items
    @GET("items")
    fun getItems(): List<Item>

}

@Module
@InstallIn(ActivityComponent::class)
object WebServiceModule {

    @Provides
    fun provideWebService(): WebService {
        return Retrofit.Builder()
//            .baseUrl("http://10.0.2.2")
            .baseUrl("http://192.168.1.71") // Miguel
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebService::class.java)
    }
}