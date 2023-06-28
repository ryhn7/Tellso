package com.example.tellso.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellso.data.AuthRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val authRepo: AuthRepoImpl
) : ViewModel() {

    suspend fun userLogin(email: String, password: String) =
        authRepo.userLogin(email, password)

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authRepo.saveAuthToken(token)
        }
    }
}