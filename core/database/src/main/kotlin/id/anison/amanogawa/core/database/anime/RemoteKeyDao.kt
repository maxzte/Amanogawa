package id.anison.amanogawa.core.database.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.anison.amanogawa.core.database.anime.entity.RemoteKeyDbo


@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(keys: List<RemoteKeyDbo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(key: RemoteKeyDbo)

    @Query("SELECT * FROM remote_key WHERE `key` = :key")
    suspend fun getKey(key: String): RemoteKeyDbo?

    @Query("SELECT * FROM remote_key WHERE lastUpdated >= :threshold")
    suspend fun getExpiredKeys(threshold: Long): List<RemoteKeyDbo>

    @Query("DELETE FROM remote_key WHERE `key` = :key")
    suspend fun deleteKey(key: String)

    @Query("select * from remote_key where `key` = :key")
    suspend fun getKeys(key: String): RemoteKeyDbo?

    @Query("delete from remote_key")
    suspend fun clearKeys()

}