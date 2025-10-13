package com.example.studysmart.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studysmart.data.local.dao.*
import com.example.studysmart.data.local.entity.*

@Database(
    entities = [
        TaskEntity::class,
        SubjectEntity::class,
        SessionEntity::class,
        ResourceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subjectDao(): SubjectDao
    abstract fun sessionDao(): SessionDao
    abstract fun resourceDao(): ResourceDao
}