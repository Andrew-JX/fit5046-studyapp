// data/local/entity/SessionEntity.kt
package com.example.studysmart.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    // Foreign key (logically associated with Subject)
    val subjectId: Long?,
    //Start/recording time (milliseconds)
    val dateMillis: Long,
    // Duration (minutes)
    val durationMinutes: Int
)
