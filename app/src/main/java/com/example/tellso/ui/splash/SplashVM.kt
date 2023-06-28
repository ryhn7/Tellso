package com.example.tellso.ui.splash

import androidx.lifecycle.ViewModel
import com.example.tellso.data.AuthRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashVM @Inject constructor(private val authRepoImpl: AuthRepoImpl) :ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepoImpl.getAuthToken()
}