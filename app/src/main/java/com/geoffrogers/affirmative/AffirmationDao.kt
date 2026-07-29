package com.geoffrogers.affirmative

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AffirmationDao {
    @Query("SELECT * FROM affirmations ORDER BY position ASC")
    fun getAll(): Flow<List<Affirmation>>

    @Insert
    suspend fun insert(affirmation: Affirmation): Long

    @Update
    suspend fun update(affirmation: Affirmation)

    @Delete
    suspend fun delete(affirmation: Affirmation)

    @Query("UPDATE affirmations SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Long, position: Int)

    @Query("SELECT COUNT(*) FROM affirmations")
    suspend fun count(): Int
}
