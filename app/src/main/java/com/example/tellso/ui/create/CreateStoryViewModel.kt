package com.example.tellso.ui.create

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.tellso.data.AuthRepo
import com.example.tellso.data.StoryRepo
import com.example.tellso.data.remote.response.FileUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class CreateStoryViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val storyRepo: StoryRepo
) : ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepo.getAuthToken()

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<FileUploadResponse>> =
        storyRepo.uploadStory(token, file, description, lat, lon)
}