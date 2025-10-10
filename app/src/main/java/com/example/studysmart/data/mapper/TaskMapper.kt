package com.example.studysmart.data.mapper
import com.example.studysmart.data.local.entity.TaskEntity
import com.example.studysmart.domain.model.Task
import com.example.studysmart.util.Priority

fun TaskEntity.toDomain() = Task(
    id = id,
    subjectId = subjectId,
    title = title,
    description = description,
    dueDateMillis = dueDateMillis,
    priority = Priority.fromInt(priority),
    isCompleted = isCompleted
)

fun Task.toEntity() = TaskEntity(
    id = id ?: 0,
    subjectId = subjectId,
    title = title,
    description = description,
    dueDateMillis = dueDateMillis,
    priority = priority.value,
    isCompleted = isCompleted
)
