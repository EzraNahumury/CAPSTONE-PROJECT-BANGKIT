package com.bangkit.trashup.data.remote.retrofit

import com.bangkit.trashup.data.remote.request.LoginRequest
import com.bangkit.trashup.data.remote.request.RegisterRequest
import com.bangkit.trashup.data.remote.request.ViewRequest
import com.bangkit.trashup.data.remote.response.ArticlesResponse
import com.bangkit.trashup.data.remote.response.LoginResponse
import com.bangkit.trashup.data.remote.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("tutorials")
    suspend fun getArticles(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): Response<ArticlesResponse>

    @POST("views")
    suspend fun updateArticleView(
        @Body viewRequest: ViewRequest
    ): Response<ArticlesResponse>
}