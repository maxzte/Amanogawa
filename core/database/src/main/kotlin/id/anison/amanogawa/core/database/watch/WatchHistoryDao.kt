package id.anison.amanogawa.core.database.watch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WatchHistoryDbo)

    @Query("SELECT * FROM watch_history ORDER BY timestamp DESC LIMIT 30")
    fun getRecentHistory(): Flow<List<WatchHistoryDbo>>

    @Query("SELECT * FROM watch_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<WatchHistoryDbo>>

    @Query("DELETE FROM watch_history WHERE animeId = :animeId")
    suspend fun deleteByAnimeId(animeId: Long)

    @Query("DELETE FROM watch_history")
    suspend fun clearAll()
}
