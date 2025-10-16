package com.example.studysmart.data.local.dao

import androidx.room.*
import com.example.studysmart.data.local.entity.ResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceDao {

    @Query("""
        SELECT * FROM resources
        WHERE (:subjectId IS NULL OR subjectId = :subjectId)
          AND (:typeName IS NULL OR type = :typeName)
          AND (:q IS NULL OR title LIKE '%' || :q || '%' OR tagsCsv LIKE '%' || :q || '%')
        ORDER BY createdAt DESC
    """)
    fun observeResources(
        subjectId: Long?,
        typeName: String?,
        q: String?
    ): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE id = :id")
    suspend fun get(id: Long): ResourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(e: ResourceEntity): Long

    @Query("DELETE FROM resources WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE resources SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)
}
