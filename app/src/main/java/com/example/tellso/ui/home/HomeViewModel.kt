package com.example.tellso.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellso.data.AuthRepo
import com.example.tellso.data.StoryRepo
import com.example.tellso.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepo: StoryRepo,
    private val authRepo: AuthRepo
) : ViewModel() {

    suspend fun getAllStories(token: String): Flow<Result<StoriesResponse>> =
        storyRepo.getAllStories(token, null, null)

}