package com.example.tellso.domain.interfaces

import androidx.paging.PagingData
import com.example.tellso.data.remote.response.FileUploadResponse
import com.example.tellso.data.remote.response.StoriesResponse
import com.example.tellso.domain.entity.Story
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface IStoryRepo {
    fun getAllStories(
        token: String,
    ): Flow<PagingData<Story>>
    fun getAllStoriesWithLocation(token: String): Flow<Result<StoriesResponse>>
    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?,
    ): Flow<Result<FileUploadResponse>>
    fun generateToken(token: String): String
}