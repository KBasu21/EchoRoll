package com.example.echorollv2.utils

import java.util.Calendar
import java.util.Locale

object DateTimeUtils {
    /**
     * Converts a time string like "09:30 AM" or "02:15 PM" into 
     * minutes since midnight (e.g., "01:00 AM" -> 60).
     */
    fun timeToMinutes(timeStr: String): Int {
        return try {
            val parts = timeStr.split(" ")
            if (parts.size != 2) return 0
            
            val timeParts = parts[0].split(":")
            var h = timeParts[0].toIntOrNull() ?: 0
            val m = if (timeParts.size > 1) timeParts[1].toIntOrNull() ?: 0 else 0
            val amPm = parts[1].uppercase(Locale.getDefault())
            
            if (amPm == "PM" && h < 12) h += 12
            if (amPm == "AM" && h == 12) h = 0
            
            h * 60 + m
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Calculates initial delay in milliseconds from now until the next 6:00 AM.
     */
    fun getDelayUntilNextSixAM(): Long {
        val now = Calendar.getInstance()
        val sixAM = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        if (now.after(sixAM)) {
            sixAM.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        return sixAM.timeInMillis - now.timeInMillis
    }
}
