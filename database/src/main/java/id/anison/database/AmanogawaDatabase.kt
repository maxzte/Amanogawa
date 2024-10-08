package id.anison.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.anison.database.dao.AnimeIDsDao
import id.anison.database.dao.CurrentTitlesDao
import id.anison.database.dao.FavoriteDao
import id.anison.database.dao.FilterDao
import id.anison.database.dao.FoundAnimeDao
import id.anison.database.dao.TitleDao
import id.anison.database.model.AnimeIDsDbo
import id.anison.database.model.CurrentTitlesDbo
import id.anison.database.model.FavoriteDbo
import id.anison.database.model.FilterDbo
import id.anison.database.model.FoundAnimeDbo
import id.anison.database.model.TitleDbo
import id.anison.database.util.Converters

class AmanogawaDatabase internal constructor(private val database: TitlesRoomDatabase) {
    val titleDao: TitleDao
        get() = database.titleDao()
    val currentTitleDao: CurrentTitlesDao
        get() = database.currentTitleDao()
    val filterDao: FilterDao
        get() = database.filterDao()
    val foundAnimeDao: FoundAnimeDao
        get() = database.foundAnimeDao()
    val favoriteDao: FavoriteDao
        get() = database.favoriteDao()
    val animeIDsDao: AnimeIDsDao
        get() = database.animeIDsDao()

}

@Database(
    entities =
    [
        TitleDbo::class,
        CurrentTitlesDbo::class,
        FilterDbo::class,
        FoundAnimeDbo::class,
        FavoriteDbo::class,
        AnimeIDsDbo::class,
    ], version = 1
)
@TypeConverters(Converters::class)
internal abstract class TitlesRoomDatabase : RoomDatabase() {
    abstract fun titleDao(): TitleDao
    abstract fun currentTitleDao(): CurrentTitlesDao
    abstract fun filterDao(): FilterDao
    abstract fun foundAnimeDao(): FoundAnimeDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun animeIDsDao(): AnimeIDsDao
}

fun AmanogawaDatabase(applicationContext: Context): AmanogawaDatabase {
    val animeRoomDatabase =
        Room.databaseBuilder(
            checkNotNull(applicationContext.applicationContext),
            TitlesRoomDatabase::class.java,
            "amanogawa"
        ).build()
    return AmanogawaDatabase(animeRoomDatabase)
}