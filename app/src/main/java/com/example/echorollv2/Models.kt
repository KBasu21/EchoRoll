package com.example.echorollv2

import androidx.compose.ui.graphics.Color

data class Subject(
    val id: String,
    val name: String,
    val category: String, // "Theory" or "Lab"
    val attended: Int,
    val missed: Int,
    val requiredPercentage: Int,
    val lastUpdated: String? = null,
    val color: Color = Color.White
)

data class RoutineEntry(
    val day: String,
    val time: String,
    val subject: String,
    val color: Color
)

data class StickyNote(
    val id: String,
    val title: String,
    val description: String
)
