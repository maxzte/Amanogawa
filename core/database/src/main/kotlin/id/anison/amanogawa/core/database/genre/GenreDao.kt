package id.anison.amanogawa.core.database.genre

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface GenreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreDbo>)

    @Query("SELECT * FROM genre WHERE name = :genreName LIMIT 1")
    suspend fun getGenreByGenreName(genreName: String): GenreDbo


}