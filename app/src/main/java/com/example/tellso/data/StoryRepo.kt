package com.example.tellso.data

import com.example.tellso.data.remote.response.StoriesResponse
import com.example.tellso.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StoryRepo @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAllStories(
        token: String,
        page: Int? = null,
        size: Int? = null
    ): Flow<Result<StoriesResponse>> = flow {
        try {
            val bearerToken = generateToken(token)
            val response = apiService.getAllStories(bearerToken, page, size)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): Flow<Result<StoriesResponse>> = flow {
        try {
            val bearerToken = generateToken(token)
            val response = apiService.uploadStory(bearerToken, file, description)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    private fun generateToken(token: String): String {
        return "Bearer $token"
    }
}