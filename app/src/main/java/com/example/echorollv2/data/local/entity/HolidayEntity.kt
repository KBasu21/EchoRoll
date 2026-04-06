package com.example.echorollv2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holidays")
data class HolidayEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // Format: yyyy-MM-dd
    val name: String,
    val type: String // "Automatic" or "Manual"
)
