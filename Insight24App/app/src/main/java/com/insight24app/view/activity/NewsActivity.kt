package com.insight24app.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.insight24app.R
import com.insight24app.databinding.ActivityNewsBinding
import com.insight24app.db.ArticleDatabase
import com.insight24app.repository.NewsRepository
import com.insight24app.util.NetworkUtils
import com.insight24app.view.viewmodel.NewsViewModel
import com.insight24app.view.NewsViewModelProviderFactory
import java.util.concurrent.TimeUnit

class NewsActivity : AppCompatActivity() {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        newsViewModel =
            ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Ask for permissions android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        // Check internet connection on activity start
        checkInternetConnection()

        scheduleBreakingNewsNotifications()
    }

    private fun checkInternetConnection() {
        if (!NetworkUtils.isInternetAvailable(this)) {
            showNoInternetDialog()
        } else {
            // Proceed with loading data from the API
        }
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("No internet connection")
            .setCancelable(false)
            .setPositiveButton("Try Again") { dialog, _ ->
                checkInternetConnection() // Check connection again
                dialog.dismiss()
            }
            .setNegativeButton("Offline") { dialog, _ ->
                val intent = Intent(this, Offline::class.java)
                startActivity(intent) // Start the activity with hardcoded articles
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
    private fun scheduleBreakingNewsNotifications() {
        val notificationWorkRequest = PeriodicWorkRequestBuilder<BreakingNewsWorker>(5, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BreakingNewsWork",
            ExistingPeriodicWorkPolicy.KEEP, // Ensures that only one instance of this work runs
            notificationWorkRequest
        )
    }
}
