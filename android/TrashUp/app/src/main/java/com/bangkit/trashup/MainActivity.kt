package com.bangkit.trashup

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bangkit.trashup.databinding.ActivityMainBinding
import com.bangkit.trashup.ui.MainViewModel
import com.bangkit.trashup.ui.ViewModelFactory
import com.bangkit.trashup.ui.welcome.WelcomeActivity
import com.bangkit.trashup.utils.WorkManagerUtil.scheduleArticleNotificationWorker

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scheduleArticleNotificationWorker(applicationContext)

        val navView: BottomNavigationView = binding.navView

        viewModel.getSession().observe(this){user ->
            if (user.token.isEmpty() || user.token == ""){
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,R.id.navigation_map,R.id.navigation_scan, R.id.navigation_articles, R.id.navigation_bookmark
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_logout -> {
                viewModel.logout()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}