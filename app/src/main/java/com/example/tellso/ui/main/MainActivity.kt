package com.example.tellso.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.paging.ExperimentalPagingApi
import com.example.tellso.R
import com.example.tellso.databinding.ActivityMainBinding
import com.example.tellso.ui.home.HomeFragment
import com.example.tellso.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding ? = null
    private val binding get() = _binding!!

    private var token: String = ""
    private val viewModel: MainVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        token = intent.getStringExtra(EXTRA_TOKEN)!!

        val navView: BottomNavigationView = binding.navView

        val navController =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)!!
                .findNavController()

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.create_story, R.id.logout
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.create_story -> {
                    supportActionBar?.hide()
                }
                R.id.logout -> {
                    viewModel.saveAuthToken("")
                    Intent(this, LoginActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
            }
        }
//        send token to home fragment
        navController.navigate(R.id.navigation_home, Bundle().apply {
            putString(HomeFragment.ARG_TOKEN, token)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
        return true
    }

    override fun onBackPressed() {
        // exit the app when the back button is pressed
        finishAffinity()
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

}