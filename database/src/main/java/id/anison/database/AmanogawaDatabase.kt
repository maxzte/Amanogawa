package id.anison.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.anison.database.anime.AnimeDao
import id.anison.database.anime.AnimeDetailsDao
import id.anison.database.anime.AnimeIdsDao
import id.anison.database.anime.RemoteKeyDao
import id.anison.database.anime.entity.AnimeDbo
import id.anison.database.anime.entity.AnimeDetailsDbo
import id.anison.database.anime.entity.RemoteKeyDbo
import id.anison.database.favorite.FavoriteDao
import id.anison.database.favorite.FavoriteDbo
import id.anison.database.filter.FilterDao
import id.anison.database.filter.FilterDbo
import id.anison.database.foundanime.FoundAnimeDao
import id.anison.database.foundanime.FoundAnimeDbo
import id.anison.database.genre.GenreDao
import id.anison.database.genre.GenreDbo
import id.anison.database.anime.entity.AnimeIdsDbo
import id.anison.database.util.Converters

/*class AmanogawaDatabase internal constructor(private val database: ARoomDatabase) {
    val animeDao: AnimeDao
        get() = database.animeDao()
    val remoteKeyDao: RemoteKeyDao
        get() = database.remoteKeyDao()
    val animeDetailsDao: AnimeDetailsDao
        get() = database.animeDetailsDao()
    val filterDao: FilterDao
        get() = database.filterDao()
    val genreDao: GenreDao
        get() = database.genreDao()
    val foundAnimeDao: FoundAnimeDao
        get() = database.foundAnimeDao()
    val favoriteDao: FavoriteDao
        get() = database.favoriteDao()
    val animeIDsDao: AnimeIDsDao
        get() = database.animeIDsDao()

    *//*suspend fun runInTransaction(transaction: suspend () -> Unit) {
        withContext(Dispatchers.IO) {
            database.runInTransaction { runBlocking { transaction() } }
        }
    }*//*
}*/

@Database(
    entities =
    [
        AnimeDbo::class,
        RemoteKeyDbo::class,
        AnimeDetailsDbo::class,
        FilterDbo::class,
        GenreDbo::class,
        FoundAnimeDbo::class,
        FavoriteDbo::class,
        AnimeIdsDbo::class,
    ], version = 1
)
@TypeConverters(Converters::class)
abstract class AmanogawaDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeDao
    abstract fun animeIdsDao(): AnimeIdsDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun animeDetailsDao(): AnimeDetailsDao
    abstract fun filterDao(): FilterDao
    abstract fun genreDao(): GenreDao
    abstract fun foundAnimeDao(): FoundAnimeDao
    abstract fun favoriteDao(): FavoriteDao
}

fun AmanogawaDatabase(applicationContext: Context): AmanogawaDatabase {
    return Room.databaseBuilder(
        checkNotNull(applicationContext.applicationContext),
        AmanogawaDatabase::class.java,
        "amanogawa"
    ).build()
}

