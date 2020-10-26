package org.feup.cmov.acmeclient.data.api

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class ApiError<out T>(val error: String) : ApiResponse<T>()
    data class NetworkError<out T>(val error: String) : ApiResponse<T>()
}

//enum class Status {
//    SUCCESS, API_ERROR, NETWORK_ERROR
//}

//data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
//    companion object {
//        fun <T> success(data: T?): Resource<T> {
//            return Resource(Status.SUCCESS, data, null)
//        }
//
//        fun <T> error(msg: String, data: T?): Resource<T> {
//            return Resource(Status.ERROR, data, msg)
//        }
//
//        fun <T> loading(data: T?): Resource<T> {
//            return Resource(Status.LOADING, data, null)
//        }
//    }
//}