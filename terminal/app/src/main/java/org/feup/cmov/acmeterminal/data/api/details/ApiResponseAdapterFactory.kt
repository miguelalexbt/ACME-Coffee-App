package org.feup.cmov.acmeterminal.data.api.details

import org.feup.cmov.acmeterminal.data.api.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResponseAdapterFactory : Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {

        if (Call::class.java != getRawType(returnType)) {
            return null
        }

        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<ApiResponse<<T>> or Call<ApiResponse<out T>>"
        }

        val responseType = getParameterUpperBound(0, returnType)
        val rawResponseType = getRawType(responseType)

        if (rawResponseType != ApiResponse::class.java) {
            return null
        }

        if (responseType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }

        val bodyType = getParameterUpperBound(0, responseType)
        return ApiResponseAdapter<Any>(bodyType)
    }
}