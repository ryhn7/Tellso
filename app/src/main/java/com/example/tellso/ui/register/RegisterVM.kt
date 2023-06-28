package com.example.tellso.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tellso.common.ResultState
import com.example.tellso.data.remote.response.RegisterResponse
import com.example.tellso.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterVM @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _registerResult = MutableLiveData<ResultState<RegisterResponse>>()
    val registerResult: LiveData<ResultState<RegisterResponse>> = _registerResult

    fun userRegister(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = ResultState.Loading

            val result = registerUseCase(name, email, password)
            _registerResult.value = result

            if (result is ResultState.Success) {
                val registerResponse = result.data
                val isSuccess = registerResponse.error
                val message = registerResponse.message
            }
        }
    }
}