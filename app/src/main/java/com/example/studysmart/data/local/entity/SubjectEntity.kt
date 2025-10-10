// data/local/entity/SubjectEntity.kt
package com.example.studysmart.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val goalHours: Float,
//    For UI gradient colors, don’t store a List<Color>, just store a key mapping
    val colorKey: Int = 0
)
