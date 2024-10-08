package id.anison.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import id.anison.database.model.TitleDbo
import kotlinx.coroutines.flow.Flow

@Dao
interface TitleDao {
    @Query("SELECT * FROM title")
    fun getAll(): List<TitleDbo>

    @Query("SELECT * FROM title ORDER BY views DESC")
    fun pagingAll(): PagingSource<Int, TitleDbo>

    @Query("SELECT * FROM title")
    fun observeAll(): Flow<List<TitleDbo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(titles: List<TitleDbo>)

    @Delete
    suspend fun remove(titles: List<TitleDbo>)

    @Query("DELETE FROM title")
    suspend fun clean()

    @Transaction
    suspend fun cleanAndInsert(titles: List<TitleDbo>){
        clean()
        insert(titles)
    }
}