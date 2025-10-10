// data/local/entity/SessionEntity.kt
package com.example.studysmart.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long?,
    val dateMillis: Long,
    val durationMin: Int
)
