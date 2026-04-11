package com.example.echorollv2.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.echorollv2.data.local.EchoDatabase
import com.example.echorollv2.data.repository.EchoRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val db = EchoDatabase.getDatabase(applicationContext)
        val repo = EchoRepository(db.echoDao())

        val calendar = Calendar.getInstance()
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val dayName = SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.time)
        
        val silentCheck = inputData.getBoolean("SILENT_CHECK", false)
        android.util.Log.d("DailyCheckWorker", "Starting daily check. Silent: $silentCheck")

        // 1. Check if today is a holiday
        val holidays = repo.allHolidays.first()
        val todayHoliday = holidays.find { it.date == dateStr }
        if (todayHoliday != null) {
            android.util.Log.d("DailyCheckWorker", "Today is a holiday: ${todayHoliday.name}. Skipping class alarms.")
            return Result.success() 
        }

        // 2. Check if today is an Exam day with suspended classes
        val examSubjects = repo.allExamSubjects.first()
        val todayExamSubject = examSubjects.find { it.examDate == dateStr }
        if (todayExamSubject != null) {
            val exams = repo.allExams.first()
            val todayExam = exams.find { it.id == todayExamSubject.examId }
            if (todayExam != null && !todayExam.classesHeldDuringExams) {
                android.util.Log.d("DailyCheckWorker", "Today is an exam day (${todayExam.name}) with suspended classes. Skipping class alarms.")
                return Result.success()
            }
        }

        // 3. Schedule 5-minute pre-end reminders for today's classes
        val routines = repo.allRoutines.first().filter { it.dayOfWeek == dayName }
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        
        android.util.Log.d("DailyCheckWorker", "Found ${routines.size} routines for $dayName")
        
        routines.forEach { routine ->
            scheduleAttendanceReminder(applicationContext, alarmManager, routine, repo)
        }

        return Result.success()
    }

    private suspend fun scheduleAttendanceReminder(
        context: Context, 
        alarmManager: android.app.AlarmManager, 
        routine: com.example.echorollv2.data.local.entity.RoutineEntity,
        repo: EchoRepository
    ) {
        try {
            // Using Locale.ENGLISH because routine times are stored as "AM/PM" string fixed in AddSubjectScreen
            val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val endTime = sdf.parse(routine.endTime) ?: return
            
            val classCalendar = Calendar.getInstance()
            val endCalendar = Calendar.getInstance().apply { time = endTime }
            
            classCalendar.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY))
            classCalendar.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE))
            classCalendar.set(Calendar.SECOND, 0)
            
            // Subtract 5 minutes
            classCalendar.add(Calendar.MINUTE, -5)
            
            // Only schedule if it's still in the future today
            if (classCalendar.timeInMillis > System.currentTimeMillis()) {
                android.util.Log.d("DailyCheckWorker", "Scheduling alarm for ${routine.subjectCode} at ${classCalendar.time}")
                val intent = android.content.Intent(context, ClassAlarmReceiver::class.java).apply {
                    putExtra("ROUTINE_ID", routine.id)
                    putExtra("SUBJECT_CODE", routine.subjectCode)
                }
                
                val pendingIntent = android.app.PendingIntent.getBroadcast(
                    context, 
                    routine.id, 
                    intent, 
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            android.app.AlarmManager.RTC_WAKEUP,
                            classCalendar.timeInMillis,
                            pendingIntent
                        )
                    } else {
                        // Fallback: use inexact alarm if permission not granted
                        alarmManager.setAndAllowWhileIdle(
                            android.app.AlarmManager.RTC_WAKEUP,
                            classCalendar.timeInMillis,
                            pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        android.app.AlarmManager.RTC_WAKEUP,
                        classCalendar.timeInMillis,
                        pendingIntent
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DailyCheckWorker", "Error scheduling alarm", e)
        }
    }
}
