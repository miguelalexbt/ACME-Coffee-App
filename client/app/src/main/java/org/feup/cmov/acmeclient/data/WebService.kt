package org.feup.cmov.acmeclient.data

import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.model.User
import org.feup.cmov.acmeclient.data.request.SignInRequest
import org.feup.cmov.acmeclient.data.request.SignUpRequest
import retrofit2.Response
import retrofit2.http.*

interface WebService {
    // Auth
    @POST("signIn")
    suspend fun signIn(
        @Body request: SignInRequest
    ): User

    @POST("signUp")
    suspend fun signUp(
        @Body request: SignUpRequest
    ) : User

    //Items
    @GET("items")
    fun getItems(@Header("User-Signature") userSignature: String): List<Item>

}
