package com.example.tellso.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellso.common.ResultState
import com.example.tellso.data.AuthRepoImpl
import com.example.tellso.data.remote.response.LoginResponse
import com.example.tellso.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authRepo: AuthRepoImpl
) : ViewModel() {
    private val _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = ResultState.Loading

            val result = loginUseCase(email, password)
            _loginResult.value = result

            if (result is ResultState.Success) {
                val loginResponse = result.data
                val token = loginResponse.loginResult.token
                saveAuthToken(token)
            }
        }
    }

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authRepo.saveAuthToken(token)
        }
    }
}