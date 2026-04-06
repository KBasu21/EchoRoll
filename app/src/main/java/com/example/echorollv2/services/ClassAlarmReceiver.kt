package com.example.echorollv2.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ClassAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val routineId = intent.getIntExtra("ROUTINE_ID", -1)
        val subjectCode = intent.getStringExtra("SUBJECT_CODE") ?: return
        
        val db = com.example.echorollv2.data.local.EchoDatabase.getDatabase(context)
        val repo = com.example.echorollv2.data.repository.EchoRepository(db.echoDao())

        // 1. Send the 5-minute pre-end reminder
        kotlinx.coroutines.MainScope().launch {
            val subject = repo.getSubjectByCode(subjectCode)
            val subjectName = subject?.name ?: subjectCode
            
            NotificationHelper.sendNotification(
                context,
                "Class Wrap-up! \uD83D\uDCDD",
                com.example.echorollv2.utils.HumorUtils.getAttendanceReminder(subjectName),
                routineId
            )

            // 2. Schedule Follow-up Worker in 15 minutes
            val workRequest = androidx.work.OneTimeWorkRequestBuilder<FollowUpWorker>()
                .setInitialDelay(15, java.util.concurrent.TimeUnit.MINUTES)
                .setInputData(
                    androidx.work.Data.Builder()
                        .putInt("ROUTINE_ID", routineId)
                        .putString("SUBJECT_CODE", subjectCode)
                        .build()
                )
                .build()
            
            androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
                "FollowUp_${routineId}",
                androidx.work.ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}
