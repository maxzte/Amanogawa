package id.anison.amanogawa

//import okhttp3.OkHttpClient
import androidx.work.WorkManager
import id.anison.api.AmanogawaApi
import id.anison.api.AmanogawaApiImpl
import id.anison.common.AndroidLogcatLogger
import id.anison.data.anime.AnimeRepo
import id.anison.data.ConnectivityRepo
import id.anison.data.favorite.FavoriteRepo
import id.anison.data.genre.GenreRepo
import id.anison.data.search.SearchRepo
import id.anison.data.settings.SettingsRepo
import id.anison.database.AmanogawaDatabase
import id.anison.feature.profile.AboutUsScreenModel
import id.anison.feature.anime.AnimeDetailsScreenModel
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import id.anison.feature.favorite.FavoriteScreenModel
import id.anison.feature.home.HomeScreenModel
import id.anison.feature.MainScreenModel
import id.anison.feature.notification.NotificationScreenModel
import id.anison.feature.search.screenmodel.RandomTitleScreenModel
import id.anison.feature.search.screenmodel.SearchScreenModel
import id.anison.feature.profile.SettingsScreenModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {
//    single { provideOkHttpClient() }
    single<AmanogawaApi> { AmanogawaApiImpl() }
    single { AmanogawaDatabase(androidContext()) }
    /*single {
        Room.databaseBuilder(
            get(),
            AmanogawaDatabase::class.java,
            "amanogawa_database"
        ).build()
    }*/

    single { AndroidLogcatLogger() }

    single { AnimeRepo(get(), get(), get()) }
    single { FavoriteRepo(get(), get(), get()) }
    single { GenreRepo(get(), get()) }
    single { SearchRepo(get(), get(), get(), get()) }
    single { ConnectivityRepo(androidContext(), get()) }
    single { SettingsRepo(androidContext()) }

    factory { HomeScreenModel(get(), get()) }
    factory { AnimeDetailsScreenModel(get(), get(), WorkManager.getInstance(get()), get()) }
    factory { SearchScreenModel(get(), get()) }
    single/*factory*/ { RandomTitleScreenModel(get()) }
    single { ExtendedSearchScreenModel(get()) }
    single/*factory*/ { FavoriteScreenModel(get(), WorkManager.getInstance(get())) }
    factory { NotificationScreenModel(get(), get()) }
    factory { AboutUsScreenModel(get()) }


    factory { SettingsScreenModel(get()) }
    single { MainScreenModel(get(), get()) }
}

/*
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(AmanogawaApiInterceptor())
        .build()
}*/
