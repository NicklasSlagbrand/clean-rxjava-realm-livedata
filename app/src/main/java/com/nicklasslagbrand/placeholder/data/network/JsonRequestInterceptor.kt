package com.nicklasslagbrand.placeholder.data.network

import okhttp3.Interceptor
import okhttp3.Response

class JsonRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request()
            .newBuilder()
            .header("accept", "application/json")
            .header("Content-Type", "application/json")

        return chain.proceed(requestBuilder.build())
    }
}
