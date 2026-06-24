package id.anison.amanogawa.core.database.di

import android.content.Context
import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.database.amanogawaDatabase
import id.anison.amanogawa.core.database.anime.AnimeDao
import id.anison.amanogawa.core.database.anime.AnimeDetailsDao
import id.anison.amanogawa.core.database.anime.CurrentAnimeDao
import id.anison.amanogawa.core.database.anime.RemoteKeyDao
import id.anison.amanogawa.core.database.favorite.FavoriteDao
import id.anison.amanogawa.core.database.filter.FilterDao
import id.anison.amanogawa.core.database.genre.GenreDao
import id.anison.amanogawa.core.database.search.SearchResultDao
import id.anison.amanogawa.core.database.teammember.TeamMemberDao
import id.anison.amanogawa.core.database.watch.WatchHistoryDao
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DatabaseModule {

    @Single
    fun provideDatabase(context: Context): AmanogawaDatabase =
        amanogawaDatabase(applicationContext = context)

    @Single
    fun animeDao(database: AmanogawaDatabase): AnimeDao = database.animeDao()

    @Single
    fun currentAnimeDao(database: AmanogawaDatabase): CurrentAnimeDao = database.currentAnimeDao()

    @Single
    fun animeDetailsDao(database: AmanogawaDatabase): AnimeDetailsDao = database.animeDetailsDao()

    @Single
    fun searchResultDao(database: AmanogawaDatabase): SearchResultDao = database.searchResultDao()

    @Single
    fun remoteKeyDao(database: AmanogawaDatabase): RemoteKeyDao = database.remoteKeyDao()

    @Single
    fun favoriteDao(database: AmanogawaDatabase): FavoriteDao = database.favoriteDao()

    @Single
    fun filterDao(database: AmanogawaDatabase): FilterDao = database.filterDao()

    @Single
    fun genreDao(database: AmanogawaDatabase): GenreDao = database.genreDao()

    @Single
    fun teamMemberDao(database: AmanogawaDatabase): TeamMemberDao = database.teamMemberDao()

    @Single
    fun watchHistoryDao(database: AmanogawaDatabase): WatchHistoryDao = database.watchHistoryDao()
}
