package org.feup.cmov.acmeterminal.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.feup.cmov.acmeterminal.data.api.ApiResponse
import org.feup.cmov.acmeterminal.data.api.SubmitOrderRequest
import org.feup.cmov.acmeterminal.data.api.SubmitOrderResponse
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val webService: WebService
) {

    suspend fun submitOrder(data: String): Resource<SubmitOrderResponse> {
        return withContext(Dispatchers.IO) {
            val split = data.split('#')

            val order = split[0]
            val userSignature = split[1].split(':')

            val userId = userSignature[0]
            val signature = userSignature[1]

            // Create request
            val request = SubmitOrderRequest(order = order)
            val response = webService.submitOrder("$userId:$signature", request)

            when (response) {
                is ApiResponse.Success -> Resource.success(response.data!!)
                is ApiResponse.ApiError -> Resource.error(response.error)
                is ApiResponse.NetworkError -> Resource.error(response.error)
            }
        }
    }

}