package com.example.tellso.ui.splash

import androidx.lifecycle.ViewModel
import com.example.tellso.data.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashVM @Inject constructor(private val authRepo: AuthRepo) :ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepo.getAuthToken()
}