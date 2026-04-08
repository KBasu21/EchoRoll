package com.example.echorollv2.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.echorollv2.data.local.EchoDatabase
import com.example.echorollv2.data.local.entity.AttendanceRecordEntity
import com.example.echorollv2.data.repository.EchoRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val routineId = intent.getIntExtra("ROUTINE_ID", -1)
        val subjectCode = intent.getStringExtra("SUBJECT_CODE") ?: return
        val status = intent.getStringExtra("STATUS") ?: return // "Present", "Absent", "Cancelled"
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)

        val db = EchoDatabase.getDatabase(context)
        val repo = EchoRepository(db.echoDao())

        MainScope().launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            // Check if record already exists for this routine on this date
            val existingRecords = repo.getAttendanceRecordsForDate(dateStr).first()
            val alreadyMarked = existingRecords.any { it.routineId == routineId && it.subjectCode == subjectCode }
            
            if (!alreadyMarked) {
                val record = AttendanceRecordEntity(
                    subjectCode = subjectCode,
                    routineId = routineId,
                    date = dateStr,
                    status = status
                )
                repo.insertAttendanceRecord(record)
                repo.recalculateSubjectStats(subjectCode)
                
                android.util.Log.d("NotificationAction", "Marked $subjectCode as $status for routine $routineId")
            }

            // Dismiss the notification
            if (notificationId != -1) {
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.cancel(notificationId)
                
                // Also cancel any follow-ups for this routine
                androidx.work.WorkManager.getInstance(context).cancelUniqueWork("FollowUp_$routineId")
            }
        }
    }
}
