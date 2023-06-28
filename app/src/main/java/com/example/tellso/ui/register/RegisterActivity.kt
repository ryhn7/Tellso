package com.example.tellso.ui.register

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
import com.example.tellso.databinding.ActivityRegisterBinding
import com.example.tellso.ui.login.LoginActivity
import com.example.tellso.utils.animateVisibility
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterVM by viewModels()
    private var registerJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActions()
        binding.passwordEditText.addTextChangedListener(passwordWatcher)

        observeRegisterResult()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        registerJob?.cancel()
    }

    private fun setActions() {
        binding.apply {
            btnLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding.btnRegister.setOnClickListener {
            registerAction()
        }
    }

    private fun registerAction() {
        val name = binding.nameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        setLoadingState(true)

        if (password.length < 8) {
            binding.btnRegister.isEnabled = false
            Toast.makeText(
                this@RegisterActivity,
                getString(R.string.disable_btn),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            binding.btnRegister.isEnabled = true
        }

        registerJob?.cancel()

        registerJob = lifecycleScope.launch {
            viewModel.userRegister(name, email, password)
        }
    }

    private fun observeRegisterResult() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is ResultState.Success -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        getString(R.string.register_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    Intent(this@RegisterActivity, LoginActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
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
                    // Do nothing
                }
            }

        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            nameEditText.isEnabled = !isLoading
            emailEditText.isEnabled = !isLoading
            passwordEditText.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading

            if (isLoading) {
                viewLoading.animateVisibility(true)
            } else {
                viewLoading.animateVisibility(false)
            }
        }
    }

    private val passwordWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            binding.btnRegister.isEnabled = s != null && s.length >= 8
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Do nothing
        }
    }
}