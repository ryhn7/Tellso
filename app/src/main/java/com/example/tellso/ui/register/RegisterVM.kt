package com.example.tellso.ui.register

import androidx.lifecycle.ViewModel
import com.example.tellso.data.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterVM @Inject constructor(
    private val authRepo: AuthRepo
) : ViewModel() {

    suspend fun userRegister(name: String, email: String, password: String) =
        authRepo.userRegister(name, email, password)
}