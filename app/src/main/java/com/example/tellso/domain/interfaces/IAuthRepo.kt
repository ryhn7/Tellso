package com.example.tellso.domain.interfaces

import com.example.tellso.data.remote.response.LoginResponse
import com.example.tellso.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

interface IAuthRepo {
    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>>
    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>>
}