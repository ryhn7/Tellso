package com.example.tellso.data

import com.example.tellso.data.local.AuthPreferences
import com.example.tellso.data.remote.response.LoginResponse
import com.example.tellso.data.remote.response.RegisterResponse
import com.example.tellso.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val apiService: ApiService
) {
    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.userRegister(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.userLogin(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveAuthToken(token: String) {
        authPreferences.saveAuthToken(token)
    }

    fun getAuthToken(): Flow<String?> {
        return authPreferences.getAuthToken()
    }
}