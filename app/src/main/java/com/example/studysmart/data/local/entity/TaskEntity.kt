// data/local/entity/TaskEntity.kt
package com.example.studysmart.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long?,
    val title: String,
    val description: String,
    val dueDateMillis: Long,
    val priority: Int,
    val isCompleted: Boolean
)
