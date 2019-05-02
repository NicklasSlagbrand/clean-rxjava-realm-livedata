package com.nicklasslagbrand.placeholder.data.network

import com.nicklasslagbrand.placeholder.Constants
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request()
            .newBuilder()
            .header("Authorization", Constants.BASIC_AUTH_HEADER_VALUE)

        return chain.proceed(requestBuilder.build())
    }
}
