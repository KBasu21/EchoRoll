package com.example.echorollv2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_replacements")
data class ClassReplacementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routineId: Int,
    val date: String, // Format: yyyy-MM-dd
    val originalSubjectCode: String,
    val replacementSubjectCode: String
)
