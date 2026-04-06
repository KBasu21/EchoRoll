package com.example.echorollv2.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sticky_notes",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["subjectCode"],
            childColumns = ["subjectCode"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectCode")]
)
data class StickyNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectCode: String,
    val title: String,
    val description: String
)
