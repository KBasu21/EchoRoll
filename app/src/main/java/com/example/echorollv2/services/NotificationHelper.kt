package com.example.echorollv2.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.echorollv2.R

object NotificationHelper {
    const val CHANNEL_ID = "echoroll_humor_channel"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "EchoRoll Humor & Alerts"
            val descriptionText = "Funny notifications about attendance and classes"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(
        context: Context, 
        title: String, 
        message: String, 
        notificationId: Int,
        subjectCode: String? = null,
        routineId: Int? = null
    ) {
        val intent = android.content.Intent(context, com.example.echorollv2.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_TODAY", true)
        }
        
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // Fallback to app icon
            .setLargeIcon(android.graphics.BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setColor(android.graphics.Color.parseColor("#4285F4")) // Echo Blue
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Add Action Buttons if it's a class reminder
        if (subjectCode != null && routineId != null) {
            val presentIntent = android.content.Intent(context, NotificationActionReceiver::class.java).apply {
                putExtra("SUBJECT_CODE", subjectCode)
                putExtra("ROUTINE_ID", routineId)
                putExtra("STATUS", "Present")
                putExtra("NOTIFICATION_ID", notificationId)
            }
            val presentPending = android.app.PendingIntent.getBroadcast(
                context, notificationId * 10 + 1, presentIntent, 
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            val absentIntent = android.content.Intent(context, NotificationActionReceiver::class.java).apply {
                putExtra("SUBJECT_CODE", subjectCode)
                putExtra("ROUTINE_ID", routineId)
                putExtra("STATUS", "Absent")
                putExtra("NOTIFICATION_ID", notificationId)
            }
            val absentPending = android.app.PendingIntent.getBroadcast(
                context, notificationId * 10 + 2, absentIntent, 
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            val cancelledIntent = android.content.Intent(context, NotificationActionReceiver::class.java).apply {
                putExtra("SUBJECT_CODE", subjectCode)
                putExtra("ROUTINE_ID", routineId)
                putExtra("STATUS", "Cancelled")
                putExtra("NOTIFICATION_ID", notificationId)
            }
            val cancelledPending = android.app.PendingIntent.getBroadcast(
                context, notificationId * 10 + 3, cancelledIntent, 
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            builder.addAction(R.mipmap.ic_launcher, "Present", presentPending)
            builder.addAction(R.mipmap.ic_launcher, "Missed", absentPending)
            builder.addAction(R.mipmap.ic_launcher, "Cancelled", cancelledPending)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}
