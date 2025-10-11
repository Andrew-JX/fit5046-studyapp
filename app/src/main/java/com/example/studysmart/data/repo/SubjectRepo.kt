package com.example.studysmart.data.repo

import android.app.Application
import com.example.studysmart.data.local.AppDatabase
import com.example.studysmart.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import com.example.studysmart.data.local.dao.SubjectDao



class subjectRepository (application: Application){
    private var subjectDao: SubjectDao = AppDatabase.getDatabase(application).subjectDao()


}