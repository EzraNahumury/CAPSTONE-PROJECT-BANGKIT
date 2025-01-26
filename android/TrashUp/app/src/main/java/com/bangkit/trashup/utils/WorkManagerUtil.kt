package com.bangkit.trashup.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bangkit.trashup.data.scheduler.MyWorker
import java.util.concurrent.TimeUnit

object WorkManagerUtil {

    fun scheduleArticleNotificationWorker(context: Context) {
        val workRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "daily_notification_work",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}
