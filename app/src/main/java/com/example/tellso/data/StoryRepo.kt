package com.example.tellso.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.tellso.data.local.entity.Story
import com.example.tellso.data.local.room.StoryDatabase
import com.example.tellso.data.remote.StoryRemoteMediator
import com.example.tellso.data.remote.response.FileUploadResponse
import com.example.tellso.data.remote.response.StoriesResponse
import com.example.tellso.data.remote.retrofit.ApiService
import com.example.tellso.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepo @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
) {

    fun getAllStories(
        token: String,
    ): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            remoteMediator = StoryRemoteMediator(
                apiService,
                storyDatabase,
                generateToken(token)
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).flow
    }

    fun getAllStoriesWithLocation(token: String): Flow<Result<StoriesResponse>> = flow {
        wrapEspressoIdlingResource {
            try {
                val bearerToken = generateToken(token)
                val response = apiService.getAllStories(bearerToken, size = 30, location = 1)
                emit(Result.success(response))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.failure(e))
            }
        }
    }

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?,
    ): Flow<Result<FileUploadResponse>> = flow {
        try {
            val bearerToken = generateToken(token)
            val response = apiService.uploadStory(bearerToken, file, description, lat, lon)
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