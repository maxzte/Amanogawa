package id.anison.amanogawa.di

import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.database.anime.AnimeDao
import id.anison.amanogawa.core.database.anime.AnimeDetailsDao
import id.anison.amanogawa.core.database.anime.CurrentAnimeDao
import id.anison.amanogawa.core.database.anime.RemoteKeyDao
import id.anison.amanogawa.core.database.favorite.FavoriteDao
import id.anison.amanogawa.core.database.filter.FilterDao
import id.anison.amanogawa.core.database.genre.GenreDao
import id.anison.amanogawa.core.database.search.SearchResultDao
import id.anison.amanogawa.core.database.teammember.TeamMemberDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val databaseModule = module {

    single { AmanogawaDatabase(applicationContext = androidContext()) }

    single<AnimeDao> {
        get<AmanogawaDatabase>().animeDao()
    }

    single<CurrentAnimeDao> {
        get<AmanogawaDatabase>().currentAnimeDao()
    }

    single<AnimeDetailsDao> {
        get<AmanogawaDatabase>().animeDetailsDao()
    }

    single<SearchResultDao> {
        get<AmanogawaDatabase>().searchResultDao()
    }

    single<RemoteKeyDao> {
        get<AmanogawaDatabase>().remoteKeyDao()
    }

    single<FavoriteDao> {
        get<AmanogawaDatabase>().favoriteDao()
    }

    single<FilterDao> {
        get<AmanogawaDatabase>().filterDao()
    }

    single<GenreDao> {
        get<AmanogawaDatabase>().genreDao()
    }

    single<TeamMemberDao> {
        get<AmanogawaDatabase>().teamMemberDao()
    }

}
