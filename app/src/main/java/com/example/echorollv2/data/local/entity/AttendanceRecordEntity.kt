package com.example.echorollv2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectCode: String,
    val routineId: Int,
    val date: String, // Format: yyyy-MM-dd
    val status: String, // "Present", "Absent", "Cancelled"
    val count: Int = 1
)
