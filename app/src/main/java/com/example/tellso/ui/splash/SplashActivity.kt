package com.example.tellso.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tellso.ui.login.LoginActivity
import com.example.tellso.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUserEntry()
    }

    private fun setupUserEntry() {
        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect() {token ->
                    if (token.isNullOrEmpty()) {
                        Intent(this@SplashActivity, LoginActivity::class.java).also { intent ->
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Intent(this@SplashActivity, MainActivity::class.java).also { intent ->
                            intent.putExtra(MainActivity.EXTRA_TOKEN, token)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }
}