package org.feup.cmov.acmeclient.data

import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.model.User
import org.feup.cmov.acmeclient.data.api.SignInRequest
import org.feup.cmov.acmeclient.data.api.SignUpRequest
import retrofit2.Response
import retrofit2.http.*

interface WebService {
    // Auth
    @POST("signIn")
    suspend fun signIn(
        @Body request: SignInRequest
    ): Response<User>

    @POST("signUp")
    suspend fun signUp(
        @Body request: SignUpRequest
    ) : User

    //Items
    @GET("item")
    suspend fun getItems(): List<Item>
//    fun getItems(@Header("User-Signature") userSignature: String): List<Item>

}
