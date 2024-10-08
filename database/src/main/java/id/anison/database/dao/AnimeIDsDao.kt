package id.anison.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.anison.database.model.AnimeIDsDbo


@Dao
interface AnimeIDsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertID(animeID: AnimeIDsDbo)

    @Query("SELECT EXISTS(SELECT 1 FROM animeIDs WHERE id = :id)")
    suspend fun isExist(id: Long): Boolean

    @Query("SELECT * FROM animeIDs")
    fun getAll(): List<AnimeIDsDbo>

    @Query("SELECT COUNT(id) FROM animeIDs")
    fun getCount(): Int

    /*@Query("SELECT maxTitles FROM animeIDs LIMIT 1")
    fun getMaxTitles(): Int*/

    @Query("DELETE FROM animeIDs")
    suspend fun deleteAllIDs()
}