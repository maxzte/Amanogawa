package id.anison.database.anime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.anison.database.anime.entity.RemoteKeyDbo


@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(keys: List<RemoteKeyDbo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(key: RemoteKeyDbo)

    @Query("select * from remote_key where id=:key")
    suspend fun getKeyByMovie(key: String): RemoteKeyDbo?

    @Query("delete from remote_key")
    suspend fun clearKeys()

}