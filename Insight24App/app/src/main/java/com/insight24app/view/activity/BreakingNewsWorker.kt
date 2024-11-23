package com.insight24app.view.activity

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class BreakingNewsWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("BreakingNewsWorker", "Worker is running")
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.sendNotification()
        return Result.success()
    }
}
