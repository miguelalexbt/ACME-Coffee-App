package org.feup.cmov.acmeterminal.data

import org.feup.cmov.acmeterminal.data.api.ApiResponse
import org.feup.cmov.acmeterminal.data.api.SubmitOrderRequest
import org.feup.cmov.acmeterminal.data.api.SubmitOrderResponse
import retrofit2.http.*

interface WebService {

    @PUT("/orders")
    suspend fun submitOrder(
        @Header("User-Signature") userSignature: String,
        @Body request: SubmitOrderRequest
    ) : ApiResponse<SubmitOrderResponse>
}