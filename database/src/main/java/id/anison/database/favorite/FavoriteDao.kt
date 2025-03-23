package id.anison.database.favorite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteDbo)

    @Query("SELECT * FROM favorite ORDER BY timestamp DESC")
    fun getAll(): Flow<List<FavoriteDbo>>

    @Query("SELECT * FROM favorite WHERE favoriteId = :favoriteId")
    suspend fun getFavoriteById(favoriteId: Long): FavoriteDbo?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE favoriteId = :favoriteId)")
    fun isFavorite(favoriteId: Long): Flow<Boolean>

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteDbo)

    @Query("DELETE FROM favorite")
    suspend fun deleteAllFavorites()

    @Update
    suspend fun update(favorite: FavoriteDbo)
}