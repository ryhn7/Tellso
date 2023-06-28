package com.example.tellso.di

import com.example.tellso.data.AuthRepoImpl
import com.example.tellso.domain.usecases.LoginUseCase
import com.example.tellso.domain.usecases.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideLoginUseCase(authRepo: AuthRepoImpl): LoginUseCase {
        return LoginUseCase(authRepo)
    }

    @Provides
    fun provideRegisterUseCase(authRepo: AuthRepoImpl): RegisterUseCase {
        return RegisterUseCase(authRepo)
    }
}