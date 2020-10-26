package org.feup.cmov.acmeclient.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.data.api.ApiResponse
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
    ): ApiResponse<User>

    @POST("signUp")
    suspend fun signUp(
        @Body request: SignUpRequest
    ) : User

    //Items
    @GET("items")
    suspend fun getItems(): ApiResponse<List<Item>>
//    fun getItems(@Header("User-Signature") userSignature: String): List<Item>

}
