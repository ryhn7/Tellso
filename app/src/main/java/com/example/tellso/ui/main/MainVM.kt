package com.example.tellso.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellso.data.AuthRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val authRepo: AuthRepoImpl,
) : ViewModel() {

    fun saveAuthToken(token: String) {
        viewModelScope.launch { authRepo.saveAuthToken(token) }
    }


}