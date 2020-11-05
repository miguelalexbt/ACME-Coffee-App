package org.feup.cmov.acmeterminal.data.api

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T?) : ApiResponse<T>()
    data class ApiError(val error: String) : ApiResponse<Nothing>()
    data class NetworkError(val error: String) : ApiResponse<Nothing>()
}
