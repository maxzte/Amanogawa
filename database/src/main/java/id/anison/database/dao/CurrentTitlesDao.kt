package id.anison.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import id.anison.database.model.CurrentTitlesDbo
import kotlinx.coroutines.flow.Flow


@Dao
interface CurrentTitlesDao {

    @Query("SELECT * FROM currentTitles")
    fun getAll(): List<CurrentTitlesDbo>

    @Query("SELECT * FROM currentTitles")
    fun pagingAll(): PagingSource<Int, CurrentTitlesDbo>

    @Query("SELECT * FROM currentTitles")
    fun observeAll(): Flow<List<CurrentTitlesDbo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currentTitles: List<CurrentTitlesDbo>)

    @Delete
    suspend fun remove(currentTitles: List<CurrentTitlesDbo>)

    @Query("DELETE FROM currentTitles")
    suspend fun clean()

    @Transaction
    suspend fun cleanAndInsert(currentTitles: List<CurrentTitlesDbo>){
        clean()
        insert(currentTitles)
    }
}