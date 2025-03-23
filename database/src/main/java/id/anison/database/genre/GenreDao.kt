package id.anison.database.genre

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.serialization.json.Json


@Dao
interface GenreDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertGenres(genres: List<GenreDbo>)

    @Query("SELECT genres FROM anime_details WHERE animeId = :animeDetailsId")
    fun getGenresByAnimeDetailsId(animeDetailsId: Long): List<String>

    suspend fun getDecodedGenresByAnimeDetailsId(animeDetailsId: Long): List<GenreDbo> {
        val jsonGenres = getGenresByAnimeDetailsId(animeDetailsId)
        return jsonGenres.flatMap { Json.decodeFromString<List<GenreDbo>>(it) }
    }

    @Query("DELETE FROM genre WHERE genreId IN (:genreIds)")
    fun deleteGenresByIds(genreIds: List<Long>): Int

    @Transaction
    suspend fun deleteGenresByAnimeDetailsId(animeDetailsId: Long) {
        val genreIds = getDecodedGenresByAnimeDetailsId(animeDetailsId).map { it.genreId }
        if (genreIds.isNotEmpty()) {
            deleteGenresByIds(genreIds)
        }
    }
}