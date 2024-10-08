package id.anison.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.anison.database.model.FoundAnimeDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface FoundAnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(anime: FoundAnimeDbo)

    @Query("SELECT * FROM foundAnime ORDER BY timestamp DESC")
    fun getAll(): Flow<List<FoundAnimeDbo>>

    @Delete
    suspend fun deleteOneFoundAnime(anime: FoundAnimeDbo)

    @Query("DELETE FROM foundAnime")
    suspend fun clearSearchHistory()
}