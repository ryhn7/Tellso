package com.example.tellso.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tellso.data.StoryRepo
import com.example.tellso.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepo: StoryRepo
) : ViewModel() {

    suspend fun getAllStories(token: String): Flow<Result<StoriesResponse>> =
        storyRepo.getAllStories(token, null, null)
}