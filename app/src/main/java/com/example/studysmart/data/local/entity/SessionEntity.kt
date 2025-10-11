// data/local/entity/SessionEntity.kt
package com.example.studysmart.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // Foreign key (logically associated with Subject)
    val subjectId: Long?,
    // Redundant display name (for historical display)
    val relatedToSubject: String,
    //Start/recording time (milliseconds)
    val dateMillis: Long,
    // Duration (minutes)
    val durationMin: Int
)
