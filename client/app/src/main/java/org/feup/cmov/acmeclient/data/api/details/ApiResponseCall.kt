package org.feup.cmov.acmeclient.data.api.details

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Request
import okio.Timeout
import org.feup.cmov.acmeclient.data.api.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class ApiResponseCall<T : Any>(private val delegate: Call<T>) : Call<ApiResponse<T>> {
    override fun enqueue(callback: Callback<ApiResponse<T>>) {
        return delegate.enqueue(object: Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val body = response.body()

                    val apiResponse = if (body != null)
                        ApiResponse.Success(body)
                    else
                        ApiResponse.ApiError("unknown error")

                    callback.onResponse(
                        this@ApiResponseCall,
                        Response.success(apiResponse)
                    )
                } else {
                    val errorBody = response.errorBody()?.string()

                    val errorMessage = if (errorBody.isNullOrEmpty())
                        response.message()
                    else
                        Gson().fromJson(errorBody, JsonObject::class.java).get("error").asString

                    callback.onResponse(
                        this@ApiResponseCall,
                        Response.success(ApiResponse.ApiError(errorMessage ?: "unknown error"))
                    )
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(
                    this@ApiResponseCall,
                    Response.success(ApiResponse.NetworkError(t.message ?: "unknown error"))
                )
            }
        })
    }

    override fun isExecuted() = delegate.isExecuted

    override fun isCanceled() = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    override fun cancel() = delegate.cancel()

    override fun execute(): Response<ApiResponse<T>> {
        throw UnsupportedOperationException("NetworkResponseCall doesn't support execute")
    }

    override fun clone() = ApiResponseCall(delegate.clone())
}