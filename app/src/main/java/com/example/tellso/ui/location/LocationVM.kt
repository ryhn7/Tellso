package com.example.tellso.ui.location

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.tellso.data.StoryRepoImpl
import com.example.tellso.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class LocationVM @Inject constructor(private val storyRepoImpl: StoryRepoImpl): ViewModel() {

    fun getAllStories(token: String): Flow<Result<StoriesResponse>> =
        storyRepoImpl.getAllStoriesWithLocation(token)
}