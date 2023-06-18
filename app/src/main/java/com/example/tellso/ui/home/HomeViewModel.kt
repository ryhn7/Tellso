package com.example.tellso.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tellso.data.StoryRepo
import com.example.tellso.data.local.entity.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepo: StoryRepo,
) : ViewModel() {

    fun getAllStories(token: String): LiveData<PagingData<Story>> =
        storyRepo.getAllStories(token).cachedIn(viewModelScope).asLiveData()
}