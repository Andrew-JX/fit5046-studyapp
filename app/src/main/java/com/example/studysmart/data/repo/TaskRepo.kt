package com.example.studysmart.data.repo

import com.example.studysmart.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepo {
    fun observeTasks(subjectId: Long? = null, onlyIncomplete: Boolean = false): Flow<List<Task>>
    suspend fun getTask(id: Long): Task?
    suspend fun upsertTask(t: Task): Long
    suspend fun deleteTask(id: Long)
    suspend fun searchTasks(keyword: String): List<Task>
    suspend fun setCompleted(id: Long, completed: Boolean)
}
