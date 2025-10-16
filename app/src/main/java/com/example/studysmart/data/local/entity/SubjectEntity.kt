// data/local/entity/SubjectEntity.kt
package com.example.studysmart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val goalHours: Float,
    val startColorArgb: Int,
    val endColorArgb: Int
)
