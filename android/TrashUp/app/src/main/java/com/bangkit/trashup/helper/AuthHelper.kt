package com.bangkit.trashup.helper

import android.content.Context
import com.bangkit.trashup.R
import com.google.auth.oauth2.GoogleCredentials
import java.io.InputStream
import android.util.Log

fun getAccessToken(context: Context): String? {
    return try {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.vertex_ai_key)

        val credentials = GoogleCredentials.fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

        credentials.refreshIfExpired()
        credentials.accessToken.tokenValue
    } catch (e: Exception) {
        Log.e("AuthHelper", "Error getting access token: ${e.message}")
        null
    }
}