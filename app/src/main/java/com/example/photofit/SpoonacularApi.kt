package com.example.photofit

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

object SpoonacularApi {
    private const val API_KEY = "6118400e8ccf460fa7a82634d94b0b54"
    private const val BASE_URL = "https://api.spoonacular.com/food/images/analyze"

    fun analyzePhoto(photo: File): String {
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                photo.name,
                photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .build()
        val request = Request.Builder()
            .url("$BASE_URL?apiKey=$API_KEY")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body?.string() ?: ""
        }
    }
}
