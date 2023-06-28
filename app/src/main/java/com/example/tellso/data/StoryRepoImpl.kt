package com.example.tellso.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.tellso.data.local.room.StoryDatabase
import com.example.tellso.data.remote.StoryRemoteMediator
import com.example.tellso.data.remote.response.FileUploadResponse
import com.example.tellso.data.remote.response.StoriesResponse
import com.example.tellso.data.remote.retrofit.ApiService
import com.example.tellso.domain.entity.Story
import com.example.tellso.domain.interfaces.IStoryRepo
import com.example.tellso.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepoImpl @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
) : IStoryRepo {

    override fun getAllStories(token: String): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            remoteMediator = StoryRemoteMediator(apiService, storyDatabase, generateToken(token)),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }

        ).flow
    }

    override fun getAllStoriesWithLocation(token: String): Flow<Result<StoriesResponse>> = flow {
        wrapEspressoIdlingResource {
            try {
                val bearerToken = generateToken(token)
                val storiesResponse = apiService.getAllStories(bearerToken, size = 30, location = 1)
                emit(Result.success(storiesResponse))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.failure(e))
            }
        }

    }


    override suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?,
    ): Flow<Result<FileUploadResponse>> = withContext(Dispatchers.IO) {
        flow {
            try {
                // Make the network call or perform any necessary operations
                val fileUploadResponse = apiService.uploadStory(
                    generateToken(token),
                    file,
                    description,
                    lat,
                    lon
                )

                // Emit the success result
                emit(Result.success(fileUploadResponse))
            } catch (e: Exception) {
                // Emit the error result
                emit(Result.failure(e))
            }
        }
    }

    override fun generateToken(token: String): String {
        return "Bearer $token"
    }
}