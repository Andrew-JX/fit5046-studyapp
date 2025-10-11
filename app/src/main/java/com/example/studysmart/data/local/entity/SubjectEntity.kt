// data/local/entity/SubjectEntity.kt
package com.example.studysmart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String,
    val goalHours: Float,
    // Use two ARGB Int to save the gradient color, and then convert it to Color in the UI layer
    val startColorArgb: Int,
    val endColorArgb: Int
)