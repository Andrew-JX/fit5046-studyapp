// data/local/dao/TaskDao.kt
package com.example.studysmart.data.local.dao
import androidx.room.*
import com.example.studysmart.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE (:subjectId IS NULL OR subjectId = :subjectId) AND (:onlyIncomplete = 0 OR isCompleted = 0) ORDER BY dueDateMillis ASC")
    fun observeTasks(subjectId: Long?, onlyIncomplete: Int = 0): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun get(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(e: TaskEntity): Long

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :q || '%' OR description LIKE '%' || :q || '%' ORDER BY dueDateMillis ASC")
    suspend fun search(q: String): List<TaskEntity>
}
