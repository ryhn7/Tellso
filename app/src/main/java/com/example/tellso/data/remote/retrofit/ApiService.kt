package com.example.tellso.data.remote.retrofit

import com.example.tellso.data.remote.response.LoginResponse
import com.example.tellso.data.remote.response.RegisterResponse
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse
}