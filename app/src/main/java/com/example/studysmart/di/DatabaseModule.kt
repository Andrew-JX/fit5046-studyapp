// di/DatabaseModule.kt
package com.example.studysmart.di
import android.content.Context
import androidx.room.Room
import com.example.studysmart.data.local.AppDatabase
import com.example.studysmart.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "studysmart.db")
            .fallbackToDestructiveMigration() // A4 阶段可用，后期再加真实 Migration
            .build()

    @Provides fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
}
