package com.example.tellso.data

import com.example.tellso.data.local.AuthPreferences
import com.example.tellso.data.remote.response.LoginResponse
import com.example.tellso.data.remote.response.RegisterResponse
import com.example.tellso.data.remote.retrofit.ApiService
import com.example.tellso.domain.interfaces.IAuthRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val apiService: ApiService
): IAuthRepo {
    override suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val registerResponse = apiService.userRegister(name, email, password)
            emit(Result.success(registerResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }

    }

    override suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val userResponse = apiService.userLogin(email, password)
            emit(Result.success(userResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    override suspend fun saveAuthToken(token: String) {
        authPreferences.saveAuthToken(token)
    }

    override fun getAuthToken(): Flow<String?> {
        return authPreferences.getAuthToken()
    }
}