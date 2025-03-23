package id.anison.database.filter

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface FilterDao {
    @Query("SELECT * FROM filter")
    fun getAll(): FilterDbo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(filters: FilterDbo)

    @Delete
    suspend fun remove(filters: FilterDbo)

    @Query("DELETE FROM filter")
    suspend fun clean()

    @Transaction
    suspend fun cleanAndInsert(filters: FilterDbo){
        clean()
        insert(filters)
    }
}