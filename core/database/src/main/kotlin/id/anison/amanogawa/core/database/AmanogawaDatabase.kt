package id.anison.amanogawa.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.anison.amanogawa.core.database.anime.AnimeDao
import id.anison.amanogawa.core.database.anime.AnimeDetailsDao
import id.anison.amanogawa.core.database.anime.CurrentAnimeDao
import id.anison.amanogawa.core.database.anime.RemoteKeyDao
import id.anison.amanogawa.core.database.anime.entity.AnimeDbo
import id.anison.amanogawa.core.database.anime.entity.AnimeDetailsDbo
import id.anison.amanogawa.core.database.anime.entity.CurrentAnimeDbo
import id.anison.amanogawa.core.database.anime.entity.RemoteKeyDbo
import id.anison.amanogawa.core.database.favorite.FavoriteDao
import id.anison.amanogawa.core.database.favorite.FavoriteDbo
import id.anison.amanogawa.core.database.filter.FilterDao
import id.anison.amanogawa.core.database.filter.FilterDbo
import id.anison.amanogawa.core.database.filter.FilterDubberCrossRef
import id.anison.amanogawa.core.database.filter.FilterGenreCrossRef
import id.anison.amanogawa.core.database.filter.FilterSoundEngineerCrossRef
import id.anison.amanogawa.core.database.filter.FilterTranslatorCrossRef
import id.anison.amanogawa.core.database.filter.FilterYearDbo
import id.anison.amanogawa.core.database.genre.GenreDao
import id.anison.amanogawa.core.database.genre.GenreDbo
import id.anison.amanogawa.core.database.search.SearchResultDao
import id.anison.amanogawa.core.database.search.SearchResultDbo
import id.anison.amanogawa.core.database.teammember.DubberDbo
import id.anison.amanogawa.core.database.teammember.SoundEngineerDbo
import id.anison.amanogawa.core.database.teammember.TeamMemberDao
import id.anison.amanogawa.core.database.teammember.TeamMemberDbo
import id.anison.amanogawa.core.database.teammember.TranslatorDbo
import id.anison.amanogawa.core.database.util.Converters
import id.anison.amanogawa.core.database.util.EnumConverter


@Database(
    entities =
    [
        AnimeDbo::class,
        CurrentAnimeDbo::class,
        RemoteKeyDbo::class,
        AnimeDetailsDbo::class,
        FavoriteDbo::class,
        SearchResultDbo::class,
        FilterDbo::class,
        GenreDbo::class,
        TeamMemberDbo::class,
        DubberDbo::class,
        TranslatorDbo::class,
        SoundEngineerDbo::class,
        FilterYearDbo::class,
        FilterGenreCrossRef::class,
        FilterDubberCrossRef::class,
        FilterTranslatorCrossRef::class,
        FilterSoundEngineerCrossRef::class,
    ], version = 1
)
@TypeConverters(Converters::class, EnumConverter::class)
abstract class AmanogawaDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeDao
    abstract fun currentAnimeDao(): CurrentAnimeDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun animeDetailsDao(): AnimeDetailsDao
    abstract fun filterDao(): FilterDao
    abstract fun genreDao(): GenreDao
    abstract fun teamMemberDao(): TeamMemberDao
    abstract fun searchResultDao(): SearchResultDao
    abstract fun favoriteDao(): FavoriteDao
}

fun AmanogawaDatabase(applicationContext: Context): AmanogawaDatabase {
    return Room.databaseBuilder(
        context = checkNotNull(applicationContext.applicationContext),
        klass = AmanogawaDatabase::class.java,
        name = "amanogawa"
    ).build()
}

