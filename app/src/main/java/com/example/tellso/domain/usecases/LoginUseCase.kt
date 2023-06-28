package com.example.tellso.domain.usecases

import com.example.tellso.common.ResultState
import com.example.tellso.data.AuthRepoImpl
import com.example.tellso.data.remote.response.LoginResponse
import kotlinx.coroutines.flow.first

class LoginUseCase(private val authRepo: AuthRepoImpl) {
    suspend operator fun invoke(email: String, password: String): ResultState<LoginResponse> {
        return try {
            val loginResult = authRepo.userLogin(email, password).first() // Get the first emission from the flow
            if (loginResult.isSuccess) {
                val loginResponse = loginResult.getOrNull()
                loginResponse?.let { ResultState.Success(it) } ?: ResultState.Error("Login response is null")
            } else {
                val exception = loginResult.exceptionOrNull()
                val errorMessage = exception?.message ?: "An error occurred"
                ResultState.Error(errorMessage)
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "An error occurred")
        }
    }
}