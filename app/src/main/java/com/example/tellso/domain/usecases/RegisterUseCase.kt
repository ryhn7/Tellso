package com.example.tellso.domain.usecases

import com.example.tellso.common.ResultState
import com.example.tellso.data.AuthRepoImpl
import com.example.tellso.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.first

class RegisterUseCase(private val authRepo: AuthRepoImpl) {
    suspend operator fun invoke(name: String, email: String, password: String) : ResultState<RegisterResponse> {
        return try {
            val registerResult = authRepo.userRegister(name, email, password).first()
            if (registerResult.isSuccess) {
                val registerResponse = registerResult.getOrNull()
                registerResponse?.let { ResultState.Success(it) } ?: ResultState.Error("Register response is null")
            } else {
                val exception = registerResult.exceptionOrNull()
                val errorMessage = exception?.message ?: "An error occurred"
                ResultState.Error(errorMessage)
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "An error occurred")
        }
    }
}