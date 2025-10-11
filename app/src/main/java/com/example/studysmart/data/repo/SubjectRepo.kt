package com.example.studysmart.data.repo

import android.app.Application
import com.example.studysmart.data.local.AppDatabase
import com.example.studysmart.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import com.example.studysmart.data.local.dao.SubjectDao
import com.example.studysmart.data.local.entity.SubjectEntity


class subjectRepository (application: Application) {
    private var subjectDao: SubjectDao = AppDatabase.getDatabase(application).subjectDao()

    val observeSubjects: Flow<List<SubjectEntity>> = subjectDao.observeSubjects()

    suspend fun listSubjects() {
        subjectDao.listSubjects()
    }

    suspend fun getSubjectById(id: Long) {
        subjectDao.get(id)
    }

    suspend fun upsert(e: SubjectEntity){
        subjectDao.upsert(e)
    }

    suspend fun delete(id: Long){
        subjectDao.delete(id)
    }


}