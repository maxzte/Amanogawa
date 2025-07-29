package id.anison.amanogawa.core.database.anime

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.anison.amanogawa.core.database.anime.entity.CurrentAnimeDbo


@Dao
interface CurrentAnimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentAnimeList(animeList: List<CurrentAnimeDbo>)

    @Query("SELECT * FROM current_anime")
    suspend fun getAll(): List<CurrentAnimeDbo>

    @Delete
    suspend fun deleteNotCurrentAnimeList(animeList: List<CurrentAnimeDbo>)

}