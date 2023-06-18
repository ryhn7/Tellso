package com.example.tellso.ui.location

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.tellso.data.StoryRepo
import com.example.tellso.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class LocationVM @Inject constructor(private val storyRepo: StoryRepo): ViewModel() {

    fun getAllStories(token: String): Flow<Result<StoriesResponse>> =
        storyRepo.getAllStoriesWithLocation(token)
}