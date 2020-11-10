package org.feup.cmov.acmeterminal.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.feup.cmov.acmeterminal.data.api.ApiResponse
import org.feup.cmov.acmeterminal.data.api.ValidateVoucherRequest
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val webService: WebService
) {

    suspend fun submitOrder(data: String) {
        withContext(Dispatchers.IO) {
            val split = data.split('#')

            val order = split[0]
            val userSignature = split[1].split(':')

            val userId = userSignature[0]
            val signature = userSignature[1]

            // Create request
            val request = ValidateVoucherRequest(order = order)
            val response = webService.submitOrder("$userId:$signature", request)

            when (response) {
                is ApiResponse.Success -> {
                    println("RESPONSE: ${response.data}")
                }
                is ApiResponse.ApiError -> {
                    println("API ERROR: ${response.error}")
                }
                is ApiResponse.NetworkError -> {
                    println("NETWORK ERROR: ${response.error}")
                }
            }
        }
    }
}