package com.example.echorollv2.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            android.util.Log.d("BootReceiver", "Device booted. Triggering DailyCheckWorker to restore alarms.")
            
            // Trigger an immediate check to restore alarms (SILENT)
            val data = androidx.work.Data.Builder().putBoolean("SILENT_CHECK", true).build()
            val request = OneTimeWorkRequestBuilder<DailyCheckWorker>()
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
