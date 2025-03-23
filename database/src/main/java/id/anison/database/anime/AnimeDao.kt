package id.anison.database.anime

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import id.anison.database.anime.entity.AnimeDbo
import id.anison.database.anime.entity.AnimeDetailsDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Query("SELECT * FROM anime")
    fun getAll(): List<AnimeDbo>

    @Query("SELECT * FROM anime ORDER BY views DESC")
    fun pagingAll(): PagingSource<Int, AnimeDbo>

    @Query("SELECT * FROM anime")
    fun observeAll(): Flow<List<AnimeDbo>>

    @Query("SELECT * FROM anime WHERE animeId = :animeId")
    suspend fun getAnimeById(animeId: Long): AnimeDbo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: AnimeDbo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(titles: List<AnimeDbo>)

    @Delete
    suspend fun remove(titles: List<AnimeDbo>)

    @Query("DELETE FROM anime")
    suspend fun clean()

    @Transaction
    suspend fun cleanAndInsert(titles: List<AnimeDbo>) {
//        clean()
        insert(titles)
    }
}