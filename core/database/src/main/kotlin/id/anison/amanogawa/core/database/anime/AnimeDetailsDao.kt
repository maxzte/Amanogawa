package id.anison.amanogawa.core.database.anime

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.anison.amanogawa.core.database.anime.entity.AnimeDetailsDbo


@Dao
interface AnimeDetailsDao {

    @Query("SELECT * FROM anime_details WHERE animeId = :animeDetailsId")
    suspend fun getAnimeDetailsById(animeDetailsId: Long): AnimeDetailsDbo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAnimeDetails(animeDetailsDbo: AnimeDetailsDbo)

    @Delete
    suspend fun deleteAnimeDetails(animeDetails: AnimeDetailsDbo)
}