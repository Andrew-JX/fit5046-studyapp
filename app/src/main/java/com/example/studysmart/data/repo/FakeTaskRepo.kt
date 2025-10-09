package com.example.studysmart.data.repo

import com.example.studysmart.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeTaskRepo @Inject constructor() : TaskRepo {
    private val idGen = AtomicLong(1)
    private val store = MutableStateFlow<List<Task>>(emptyList())

    override fun observeTasks(subjectId: Long?): Flow<List<Task>> =
        if (subjectId == null) store else store.map { it.filter { t -> t.subjectId == subjectId } }

    override suspend fun getTask(id: Long): Task? = store.value.find { it.id == id }

    override suspend fun upsertTask(t: Task): Long {
        val id = t.id ?: idGen.getAndIncrement()
        val newT = t.copy(id = id)
        store.value = store.value.filterNot { it.id == id } + newT
        return id
    }

    override suspend fun deleteTask(id: Long) {
        store.value = store.value.filterNot { it.id == id }
    }

    override suspend fun searchTasks(keyword: String): List<Task> =
        store.value.filter { it.title.contains(keyword, true) || it.description.contains(keyword, true) }
}
