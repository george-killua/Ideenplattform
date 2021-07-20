package com.killua.ideenplattform.applicationmanager

import okhttp3.Interceptor
import okhttp3.Response


class AuthenticationInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", token)
        val request = builder.build()
        return chain.proceed(request)
    }
}
