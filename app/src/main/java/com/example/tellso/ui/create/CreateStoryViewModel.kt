package com.example.tellso.ui.create

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.tellso.data.AuthRepoImpl
import com.example.tellso.data.StoryRepoImpl
import com.example.tellso.data.remote.response.FileUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class CreateStoryViewModel @Inject constructor(
    private val authRepo: AuthRepoImpl,
    private val storyRepoImpl: StoryRepoImpl
) : ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepo.getAuthToken()

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<FileUploadResponse>> =
        storyRepoImpl.uploadStory(token, file, description, lat, lon)
}