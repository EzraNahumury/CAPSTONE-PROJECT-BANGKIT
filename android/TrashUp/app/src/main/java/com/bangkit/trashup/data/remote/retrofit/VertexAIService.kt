package com.bangkit.trashup.data.remote.retrofit

import com.bangkit.trashup.data.remote.response.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface VertexAIService {
    @Headers("Content-Type: application/json")
    @POST("v1/projects/capstone-441912/locations/us-central1/publishers/google/models/gemini-1.5-pro:streamGenerateContent")
    suspend fun generateContent(
        @Body requestBody: com.bangkit.trashup.data.remote.request.RequestBody
    ): Response<ResponseBody>
}