package id.anison.amanogawa.di

import android.content.Context
import coil.ImageLoader
import coil.util.DebugLogger
import id.anison.amanogawa.core.data.di.DataModule
import id.anison.amanogawa.core.notification.di.NotificationModule
import id.anison.amanogawa.core.work.di.WorkerModule
import id.anison.amanogawa.feature.anime.di.AnimeModule
import id.anison.amanogawa.feature.favorite.di.FavoriteModule
import id.anison.amanogawa.feature.home.di.HomeModule
import id.anison.amanogawa.feature.profile.di.ProfileModule
import id.anison.amanogawa.feature.search.di.SearchModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(
    includes = [
        DataModule::class,
        AnimeModule::class,
        FavoriteModule::class,
        HomeModule::class,
        ProfileModule::class,
        SearchModule::class,
        NotificationModule::class,
        WorkerModule::class,
    ]
)
@ComponentScan("id.anison.amanogawa")
class AppModule {

    @Single
    fun defaultImageLoader(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .crossfade(true)
            .logger(DebugLogger())
            .build()

}
