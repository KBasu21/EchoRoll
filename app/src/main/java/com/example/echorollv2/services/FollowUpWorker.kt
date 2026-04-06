package com.example.echorollv2.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.echorollv2.data.local.EchoDatabase
import com.example.echorollv2.data.repository.EchoRepository
import com.example.echorollv2.utils.HumorUtils
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FollowUpWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val routineId = inputData.getInt("ROUTINE_ID", -1)
        val subjectCode = inputData.getString("SUBJECT_CODE") ?: return Result.failure()

        val db = EchoDatabase.getDatabase(applicationContext)
        val repo = EchoRepository(db.echoDao())

        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val records = repo.getAttendanceRecordsForDate(dateStr).first()
        
        val recordExists = records.any { it.routineId == routineId && it.subjectCode == subjectCode }

        if (!recordExists) {
            val subject = repo.getSubjectByCode(subjectCode)
            val subjectName = subject?.name ?: subjectCode
            
            NotificationHelper.sendNotification(
                applicationContext,
                "Ghost Detector \uD83D\uDC7B",
                HumorUtils.getRandomNotification(subjectName),
                subjectCode.hashCode() + 100
            )

            // Self-reschedule in 15 minutes
            val workRequest = androidx.work.OneTimeWorkRequestBuilder<FollowUpWorker>()
                .setInitialDelay(15, java.util.concurrent.TimeUnit.MINUTES)
                .setInputData(
                    androidx.work.Data.Builder()
                        .putInt("ROUTINE_ID", routineId)
                        .putString("SUBJECT_CODE", subjectCode)
                        .build()
                )
                .build()
            
            androidx.work.WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                "FollowUp_${routineId}",
                androidx.work.ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        return Result.success()
    }
}
