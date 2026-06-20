package id.anison.amanogawa.core.database.search

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResult(anime: SearchResultDbo)

    @Query("SELECT * FROM search_result ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SearchResultDbo>>

    @Delete
    suspend fun deleteSearchResult(anime: SearchResultDbo)

    @Query("DELETE FROM search_result")
    suspend fun clearSearchHistory()

}