package com.example.echorollv2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val subjectCode: String, // E.g., "CS-301" (Unique ID)
    val name: String,
    val category: String, // "Theory" or "Lab"
    val professorName: String,
    val attended: Int,
    val missed: Int,
    val requiredPercentage: Int
)