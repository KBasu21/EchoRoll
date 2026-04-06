package com.example.echorollv2.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "routines",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["subjectCode"],
            childColumns = ["subjectCode"],
            onDelete = ForeignKey.CASCADE // 💥 Swiping delete on a subject deletes its routine automatically!
        )
    ],
    indices = [Index("subjectCode")]
)
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectCode: String, // Links back to the SubjectEntity
    val dayOfWeek: String,   // E.g., "Monday"
    val startTime: String,   // E.g., "09:00 AM"
    val endTime: String      // E.g., "10:00 AM"
)