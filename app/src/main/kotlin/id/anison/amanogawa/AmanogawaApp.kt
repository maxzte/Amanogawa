package id.anison.amanogawa

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import id.anison.amanogawa.core.notification.Notification
import id.anison.amanogawa.core.work.Work
import id.anison.amanogawa.di.AppModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [AppModule::class])
class MyApp

class AmanogawaApp : Application(), ImageLoaderFactory {
    private val imageLoader: ImageLoader by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin<MyApp> {
            androidLogger()
            androidContext(this@AmanogawaApp)
            workManagerFactory()
        }

        Notification.init(this)
        Work.init(this)

    }

    override fun newImageLoader(): ImageLoader {
        return imageLoader
    }

}
