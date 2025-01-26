package com.bangkit.trashup.data.remote.retrofit

import android.content.Context
import android.util.Log
import com.bangkit.trashup.BuildConfig
import com.bangkit.trashup.data.pref.UserPreference
import com.bangkit.trashup.data.pref.dataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(context: Context): ApiService {
            val userPreference = UserPreference.getInstance(context.dataStore)

            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val authInterceptor = Interceptor { chain ->
                val token = runBlocking {
                    userPreference.getSession().firstOrNull()?.token.orEmpty()
                }

                val requestBuilder = chain.request().newBuilder()

                if (token.isNotEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                val request = requestBuilder.build()
                Log.d("Auth Header", request.headers["Authorization"].toString())
                chain.proceed(request)
            }

            val apiKeyInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("api-key", BuildConfig.API_KEY)
                    .build()
                Log.d("API Key Header", request.headers["api-key"].toString())
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .addInterceptor(apiKeyInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
