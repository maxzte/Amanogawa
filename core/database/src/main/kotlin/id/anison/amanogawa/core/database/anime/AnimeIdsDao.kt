package id.anison.amanogawa.core.database.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.anison.amanogawa.core.database.anime.entity.AnimeIdsDbo


@Dao
interface AnimeIdsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeIds(animeIdsDbo: AnimeIdsDbo)

    @Query("SELECT EXISTS(SELECT 1 FROM anime_ids WHERE id = :id)")
    suspend fun isExist(id: Long): Boolean

    @Query("SELECT * FROM anime_ids")
    fun getAll(): List<AnimeIdsDbo>

    @Query("SELECT COUNT(id) FROM anime_ids")
    fun getCount(): Int

    @Query("DELETE FROM anime_ids")
    suspend fun deleteAllIDs()
}