package com.example.studysmart.data.repo

import com.example.studysmart.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepo {
    fun observeSubjects(): Flow<List<Subject>>
    suspend fun listSubjects(): List<Subject>
    suspend fun getSubject(id: Long): Subject?
    suspend fun upsertSubject(s: Subject): Long
    suspend fun deleteSubject(id: Long)
}
