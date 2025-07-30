package id.anison.amanogawa.core.database.filter

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface FilterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilter(filter: FilterDbo)

    @Insert
    suspend fun insertYears(years: List<FilterYearDbo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenreRefs(refs: List<FilterGenreCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDubberRefs(refs: List<FilterDubberCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslatorRefs(refs: List<FilterTranslatorCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoundEngineerRefs(refs: List<FilterSoundEngineerCrossRef>)

    @Query("DELETE FROM filter_genre_cross_ref WHERE filterType = :filterType")
    suspend fun deleteGenreRefs(filterType: FilterTypeDbo)

    @Query("DELETE FROM filter_dubber_cross_ref WHERE filterType = :filterType")
    suspend fun deleteDubberRefs(filterType: FilterTypeDbo)

    @Query("DELETE FROM filter_translator_cross_ref WHERE filterType = :filterType")
    suspend fun deleteTranslatorRefs(filterType: FilterTypeDbo)

    @Query("DELETE FROM filter_sound_engineer_cross_ref WHERE filterType = :filterType")
    suspend fun deleteSoundEngineerRefs(filterType: FilterTypeDbo)

    @Query("DELETE FROM filter_year WHERE filterType = :filterType")
    suspend fun deleteYears(filterType: FilterTypeDbo)

    @Query("SELECT * FROM `filter` WHERE filterType = :filterType")
    suspend fun getFilter(filterType: FilterTypeDbo): FilterDbo?

    @Transaction
    @Query("SELECT * FROM `filter` WHERE filterType = :filterType")
    suspend fun getFilterWithGenres(filterType: FilterTypeDbo): FilterWithGenres

    @Transaction
    @Query("SELECT * FROM `filter` WHERE filterType = :filterType")
    suspend fun getFilterWithDubbers(filterType: FilterTypeDbo): FilterWithDubbers

    @Transaction
    @Query("SELECT * FROM `filter` WHERE filterType = :filterType")
    suspend fun getFilterWithTranslators(filterType: FilterTypeDbo): FilterWithTranslators

    @Transaction
    @Query("SELECT * FROM `filter` WHERE filterType = :filterType")
    suspend fun getFilterWithSoundEngineers(filterType: FilterTypeDbo): FilterWithSoundEngineers

    @Transaction
    @Query("SELECT * FROM `filter` WHERE filterType = :filterType")
    suspend fun getFilterWithYears(filterType: FilterTypeDbo): FilterWithYears

    @Delete
    suspend fun remove(filters: FilterDbo)

    @Query("DELETE FROM `filter`")
    suspend fun clear()

}