package org.feup.cmov.acmeclient.data

import org.feup.cmov.acmeclient.data.api.ApiResponse
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.model.User
import org.feup.cmov.acmeclient.data.api.SignInRequest
import org.feup.cmov.acmeclient.data.api.SignUpRequest
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
    ) : ApiResponse<User>

    //Items
    @GET("items")
    suspend fun getItems(): ApiResponse<List<Item>>

    companion object {
        @Target(AnnotationTarget.FUNCTION)
        @Retention(AnnotationRetention.RUNTIME)
        annotation class AuthenticatedRequest
    }
}
