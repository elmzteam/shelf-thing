package com.elmz.shelfthing.util

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Api {
	@Multipart
	@POST("/status")
	fun upload(@Part bytes: List<MultipartBody.Part>): Call<List<String>>
}
