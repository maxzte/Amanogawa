package id.anison.amanogawa

//import okhttp3.OkHttpClient
import androidx.work.WorkManager
import id.anison.api.AmanogawaApi
import id.anison.api.AmanogawaApiImpl
import id.anison.common.AndroidLogcatLogger
import id.anison.data.repository.AmanogawaRepository
import id.anison.data.repository.ConnectivityRepository
import id.anison.data.repository.SettingsRepository
import id.anison.database.AmanogawaDatabase
import id.anison.feature.profile.AboutUsScreenModel
import id.anison.feature.anime.AnimeScreenModel
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import id.anison.feature.favorite.FavoriteScreenModel
import id.anison.feature.home.HomeScreenModel
import id.anison.feature.common.MainScreenModel
import id.anison.feature.search.screenmodel.RandomTitleScreenModel
import id.anison.feature.search.screenmodel.SearchScreenModel
import id.anison.feature.profile.SettingsScreenModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {
//    single { provideOkHttpClient() }
    single<AmanogawaApi> { AmanogawaApiImpl() }
    single { AmanogawaDatabase(androidContext()) }
    single { AndroidLogcatLogger() }
    single { AmanogawaRepository(get(), get(), get()) }
    single { ConnectivityRepository(androidContext(), get()) }

    factory { HomeScreenModel(get(), get()) }
    factory { AnimeScreenModel(get(), WorkManager.getInstance(get()), get()) }
    factory { SearchScreenModel(get(), get()) }
    single/*factory*/ { RandomTitleScreenModel(get()) }
    single { ExtendedSearchScreenModel(get(), get()) }
    factory { FavoriteScreenModel(get(), WorkManager.getInstance(get())) }
    factory { AboutUsScreenModel(get()) }

    single { SettingsRepository(androidContext()) }
    factory { SettingsScreenModel(get()) }
    single { MainScreenModel(get()) }
}

//fun provideOkHttpClient(): OkHttpClient {
//    return OkHttpClient.Builder()
//        .addInterceptor(AmanogawaApiInterceptor())
//        .build()
//}