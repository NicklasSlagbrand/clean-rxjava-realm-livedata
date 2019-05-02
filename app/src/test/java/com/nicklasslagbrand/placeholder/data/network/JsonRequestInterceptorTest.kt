package com.nicklasslagbrand.placeholder.data.network

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Request
import org.amshove.kluent.shouldEqual
import org.junit.Test

class JsonRequestInterceptorTest {
    private val requestInterceptor = JsonRequestInterceptor()

    @Test
    fun `verify JsonRequestInterceptor adds json content and accept headers to any request`() {
        val testChain = mockk<Interceptor.Chain>()
        // Catch request passed into Chain.proceed(request) method.
        val argument = slot<Request>()
        // Simple empty request.
        val request = Request.Builder().url("http://some/url")
            .build()
        every { testChain.request() }.returns(request)
        every { testChain.proceed(capture(argument)) }.returns(mockk())

        requestInterceptor.intercept(testChain)

        argument.captured.header("accept").shouldEqual("application/json")
        argument.captured.header("Content-Type").shouldEqual("application/json")
    }
}
