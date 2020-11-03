package org.feup.cmov.acmeclient.data

import org.feup.cmov.acmeclient.data.api.ApiResponse
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.api.SignUpRequest
import org.feup.cmov.acmeclient.data.api.SignUpResponse
import org.feup.cmov.acmeclient.data.model.Voucher
import retrofit2.http.*

interface WebService {
    // Sign up
    @POST("/signUp")
    suspend fun signUp(@Body request: SignUpRequest) : ApiResponse<SignUpResponse>

    // Items
    @AuthenticatedRequest
    @GET("/items")
    suspend fun getItems(@Query("lastUpdate") lastUpdate: String?): ApiResponse<List<Item>>

    // Vouchers
    @AuthenticatedRequest
    @GET("/vouchers")
    suspend fun getVouchers(@Query("userId") userId: String): ApiResponse<List<Voucher>>

    companion object {
        @Target(AnnotationTarget.FUNCTION)
        @Retention(AnnotationRetention.RUNTIME)
        annotation class AuthenticatedRequest
    }
}
