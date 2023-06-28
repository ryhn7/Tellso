package com.example.tellso.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.example.tellso.R
import com.example.tellso.common.ResultState
import com.example.tellso.databinding.ActivityLoginBinding
import com.example.tellso.ui.main.MainActivity
import com.example.tellso.ui.register.RegisterActivity
import com.example.tellso.utils.animateVisibility
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginVM by viewModels()
    private var loginJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        binding.passwordEditText.addTextChangedListener(passwordWatcher)

        observeLoginResult()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        loginJob?.cancel()
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

        loginJob?.cancel()

        loginJob = lifecycleScope.launch {
            viewModel.login(email, password)
        }
    }

    private fun observeLoginResult() {
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    val loginResponse = result.data
                    val token = loginResponse.loginResult.token
                    viewModel.saveAuthToken(token)

                    Intent(this@LoginActivity, MainActivity::class.java).also { intent ->
                        intent.putExtra(MainActivity.EXTRA_TOKEN, token)
                        startActivity(intent)
                        finish()
                    }

                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.login_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ResultState.Error -> {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_message),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    setLoadingState(false)
                }
                is ResultState.Loading -> {
                    setLoadingState(true)
                }
                else -> {}
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            emailEditText.isEnabled = !isLoading
            passwordEditText.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading

            if (isLoading) {
                viewLoading.animateVisibility(true)
            } else {
                viewLoading.animateVisibility(false)
            }
        }
    }

    //    will close the app when back button is pressed
    override fun onBackPressed() {
        super.onBackPressedDispatcher.onBackPressed()
        finishAffinity()
    }

    private val passwordWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            binding.btnLogin.isEnabled = s != null && s.length >= 8
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Do nothing
        }
    }
}