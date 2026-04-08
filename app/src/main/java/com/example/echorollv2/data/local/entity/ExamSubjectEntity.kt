package com.example.echorollv2.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exam_subjects",
    foreignKeys = [
        ForeignKey(
            entity = ExamEntity::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("examId")]
)
data class ExamSubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examId: Int,
    val subjectCode: String,
    val subjectName: String,
    val examDate: String, // Format: yyyy-MM-dd
    val marksScored: String = "",
    val stickyNote: String = ""
)
