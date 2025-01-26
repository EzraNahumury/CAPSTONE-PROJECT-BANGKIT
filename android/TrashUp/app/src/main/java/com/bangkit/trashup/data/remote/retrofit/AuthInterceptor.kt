package com.bangkit.trashup.data.remote.retrofit

import android.content.Context
import com.bangkit.trashup.helper.getAccessToken
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    @Suppress("FoldInitializerAndIfToElvis")
    override fun intercept(chain: Interceptor.Chain): Response {
        val authToken = getAccessToken(context)

        if (authToken == null) {
            throw Exception("Failed to retrieve access token")
        }

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $authToken")
            .build()

        return chain.proceed(request)
    }
}