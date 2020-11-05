package org.feup.cmov.acmeterminal.data.api.details

import org.feup.cmov.acmeterminal.data.api.ApiResponse
import retrofit2.*
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ApiResponseAdapter<T : Any>(
    private val responseType : Type
) : CallAdapter<T, Call<ApiResponse<T>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Call<ApiResponse<T>> {
        return ApiResponseCall(call)
    }
}
