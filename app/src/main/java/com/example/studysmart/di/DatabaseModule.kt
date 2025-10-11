// di/DatabaseModule.kt
package com.example.studysmart.di
import android.content.Context
import androidx.room.Room
import com.example.studysmart.data.local.AppDatabase
import com.example.studysmart.data.local.dao.SessionDao
import com.example.studysmart.data.local.dao.SubjectDao
import com.example.studysmart.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "studysmart.db")

            // Available during development

            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideSubjectDao(db: AppDatabase): SubjectDao = db.subjectDao()
    @Provides fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
    @Provides fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
}