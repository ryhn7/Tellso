package com.example.tellso.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tellso.ui.login.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(this, LoginActivity::class.java).also {
            runBlocking {
                delay(500)
                startActivity(it)
                finish()
            }
        }
    }
}