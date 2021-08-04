package com.killua.ideenplattform.di

import okhttp3.*


class AuthenticationInterceptor(private val token: String) : Authenticator {


    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") != null) {
            return null // Give up, we've already attempted to authenticate.
        }

        return response.request.newBuilder()
            .header("Authorization", token)
            .build()
    }
}
