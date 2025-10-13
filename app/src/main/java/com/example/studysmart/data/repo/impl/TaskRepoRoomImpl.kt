package com.example.studysmart.data.repo.impl
import com.example.studysmart.data.local.dao.TaskDao
import com.example.studysmart.data.mapper.toDomain
import com.example.studysmart.data.mapper.toEntity
import com.example.studysmart.data.repo.TaskRepo
import com.example.studysmart.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepoRoomImpl @Inject constructor(
    private val dao: TaskDao
) : TaskRepo {
    override fun observeTasks(subjectId: Long?, onlyIncomplete: Boolean): Flow<List<Task>> =
        dao.observeTasks(subjectId, if (onlyIncomplete) 1 else 0).map { it.map { e -> e.toDomain() } }

    override suspend fun getTask(id: Long): Task? = dao.get(id)?.toDomain()

    override suspend fun upsertTask(t: Task): Long = dao.upsert(t.toEntity())

    override suspend fun deleteTask(id: Long) = dao.delete(id)

    override suspend fun searchTasks(keyword: String): List<Task> =
        dao.search(keyword).map { it.toDomain() }

    override suspend fun setCompleted(id: Long, completed: Boolean) =
        dao.setCompleted(id, completed)
}
