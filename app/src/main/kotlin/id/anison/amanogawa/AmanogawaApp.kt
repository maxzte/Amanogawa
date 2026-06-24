package id.anison.amanogawa

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import id.anison.amanogawa.core.notification.Notification
import id.anison.amanogawa.core.work.Work
import id.anison.amanogawa.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [AppModule::class])
class MyApp

class AmanogawaApp : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }

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

}
