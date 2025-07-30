package id.anison.amanogawa.di

import coil.ImageLoader
import coil.request.CachePolicy
import coil.util.DebugLogger
import id.anison.amanogawa.MainScreenModel
import id.anison.amanogawa.core.common.AndroidLogcatLogger
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.data.ConnectivityRepo
import id.anison.amanogawa.core.data.anime.AnimeRepo
import id.anison.amanogawa.core.data.anime.AnimeRepoImpl
import id.anison.amanogawa.core.data.anime.RemoteKeyRepo
import id.anison.amanogawa.core.data.favorite.FavoriteRepo
import id.anison.amanogawa.core.data.favorite.FavoriteRepoImpl
import id.anison.amanogawa.core.data.filter.FilterRepo
import id.anison.amanogawa.core.data.filter.FilterRepoImpl
import id.anison.amanogawa.core.data.search.SearchRepo
import id.anison.amanogawa.core.data.search.SearchRepoImpl
import id.anison.amanogawa.core.data.settings.SettingsRepo
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.network.AmanogawaApi
import id.anison.amanogawa.core.network.AmanogawaApiImpl
import id.anison.amanogawa.core.notification.NewEpisodesNotifier
import id.anison.amanogawa.core.notification.Notifier
import id.anison.amanogawa.feature.anime.AnimeDetailsScreenModel
import id.anison.amanogawa.feature.favorite.FavoriteScreenModel
import id.anison.amanogawa.feature.home.HomeScreenModel
import id.anison.amanogawa.feature.profile.AboutUsScreenModel
import id.anison.amanogawa.feature.profile.AppearanceScreenModel
import id.anison.amanogawa.feature.profile.ProfileScreenModel
import id.anison.amanogawa.feature.profile.SettingsScreenModel
import id.anison.amanogawa.feature.search.screenmodel.FilterResultScreenModel
import id.anison.amanogawa.feature.search.screenmodel.FilterScreenModel
import id.anison.amanogawa.feature.search.screenmodel.RandomAnimeScreenModel
import id.anison.amanogawa.feature.search.screenmodel.SearchScreenModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single<AmanogawaApi> { AmanogawaApiImpl() }

    single<Logger> { AndroidLogcatLogger() }

    single<ImageLoader>(named("default")) {
        ImageLoader.Builder(androidContext())
            .crossfade(true)
            .logger(DebugLogger())
            .build()
    }

    single<ImageLoader>(named("noCache")) {
        ImageLoader.Builder(androidContext())
            .crossfade(true)
            .diskCachePolicy(CachePolicy.DISABLED)
            .logger(DebugLogger())
            .build()
    }

    single<AnimeRepo> {
        AnimeRepoImpl(
            database = get(),
            api = get(),
            log = get(),
        )
    }
    single {
        RemoteKeyRepo(
            database = get(),
            log = get(),
        )
    }
    single<FavoriteRepo> {
        FavoriteRepoImpl(
            favoriteDao = get(),
            log = get(),
        )
    }
    single<FilterRepo> {
        FilterRepoImpl(
            api = get(),
            database = get(),
            log = get(),
        )
    }
    single<SearchRepo> {
        SearchRepoImpl(
            api = get(),
            database = get(),
            log = get(),
        )
    }

    single { ConnectivityRepo(context = androidContext(), log = get()) }
    single { SettingsRepo(context = androidContext(), api = get(), log = get()) }

    single { HomeScreenModel(animeRepo = get(), log = get()) }
    factory { (animeId: Long) ->
        AnimeDetailsScreenModel(
            animeId = animeId,
            animeRepo = get(),
            favoriteRepo = get(),
            log = get(),
        )
    }
    single { SearchScreenModel(searchRepo = get(), log = get()) }
    factory { RandomAnimeScreenModel(searchRepo = get(), log = get()) }
    factory { FilterScreenModel(filterRepo = get(), log = get()) }
    factory { (filterId: Long?, filterName: String, filterType: FilterTypeUI) ->
        FilterResultScreenModel(
            filterParamId = filterId,
            filterName = filterName,
            filterType = filterType,
            searchRepo = get(),
            filterRepo = get(),
            log = get(),
        )
    }
    single {
        FavoriteScreenModel(
            favoriteRepo = get(),
            log = get(),
        )
    }
    factory { ProfileScreenModel(log = get()) }
    factory { SettingsScreenModel(settingsRepo = get(), log = get()) }
    factory { AppearanceScreenModel(settingsRepo = get(), log = get()) }
    factory { AboutUsScreenModel(settingsRepo = get(), log = get()) }
    single { MainScreenModel(settingsRepo = get(), connectivityRepo = get(), log = get()) }

    single<Notifier> {
        NewEpisodesNotifier(
            context = androidContext(),
            log = get(),
        )
    }

}
