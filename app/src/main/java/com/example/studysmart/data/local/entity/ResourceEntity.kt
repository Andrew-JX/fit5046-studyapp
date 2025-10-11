package com.example.studysmart.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resources",
    indices = [
        Index(value = ["title"]),
        Index(value = ["subjectId"]),
        Index(value = ["type"])
    ]
)
data class ResourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val title: String,
    val url: String,
    val type: String,          // ResourceType.name
    val subjectId: Long?,
    val provider: String?,
    val note: String?,
    val tagsCsv: String,       // "math,algebra"
    val isFavorite: Boolean,
    val createdAt: Long
)
