package com.example.tellso.data.remote.retrofit

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.tellso.BuildConfig.API_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {
        fun getApiService(context: Context): ApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor(ChuckerInterceptor(context))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}