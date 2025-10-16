// di/RepoModule.kt
package com.example.studysmart.di
import com.example.studysmart.data.repo.*
import com.example.studysmart.data.repo.impl.ResourceRepoRoomImpl
import com.example.studysmart.data.repo.impl.ResourcesRepoRetrofit
import com.example.studysmart.data.repo.impl.SessionRepoRoomImpl
import com.example.studysmart.data.repo.impl.SubjectRepoRoomImpl
import com.example.studysmart.data.repo.impl.TaskRepoRoomImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds @Singleton
    abstract fun bindTaskRepo(impl: com.example.studysmart.data.repo.impl.TaskRepoRoomImpl): TaskRepo

    @Binds @Singleton
    abstract fun bindSubjectRepo(impl: SubjectRepoRoomImpl): SubjectRepo

    @Binds @Singleton
    abstract fun bindSessionRepo(impl: SessionRepoRoomImpl): SessionRepo

    @Binds @Singleton
    abstract fun bindResourceRepo(impl: ResourceRepoRoomImpl): ResourceRepo


}