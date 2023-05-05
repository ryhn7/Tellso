package com.example.tellso.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tellso.databinding.ActivityLoginBinding
import com.example.tellso.ui.main.MainActivity
import com.example.tellso.ui.register.RegisterActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginVM by viewModels()
    private var loginJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
    }

    private fun setActions() {
        binding.apply {
            btnSignup.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            btnLogin.setOnClickListener {
                loginAction()
            }
        }
    }

    private fun loginAction() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        setLoadingState(true)

        lifecycleScope.launchWhenResumed {
            if (loginJob.isActive) {
                loginJob.cancelChildren()
            }

            loginJob = launch {
                viewModel.userLogin(email, password).collect() { result ->
                    result.onSuccess { credentials ->

                        credentials.loginResult?.token?.let { token ->
                            viewModel.saveAuthToken(token)
                            Intent(this@LoginActivity, MainActivity::class.java).also { intent ->
                                intent.putExtra(MainActivity.EXTRA_TOKEN, token)
                                startActivity(intent)
                                finish()
                            }
                        }

                        Toast.makeText(
                            this@LoginActivity,
                            "Login Success",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    result.onFailure {
                        Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        setLoadingState(false)
                    }

                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            emailEditText.isEnabled = !isLoading
            passwordEditText.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading

            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }
}