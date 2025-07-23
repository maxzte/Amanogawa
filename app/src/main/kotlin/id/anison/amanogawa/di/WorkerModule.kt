package id.anison.amanogawa.di

import id.anison.amanogawa.core.work.CacheCleanUpWorker
import id.anison.amanogawa.core.work.NotificationWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker {
        CacheCleanUpWorker(
            context = androidContext(),
            workerParams = get(),
            remoteKeyRepo = get(),
            log = get(),
        )
    }

    worker {
        NotificationWorker(
            context = androidContext(),
            workerParams = get(),
            animeRepo = get(),
            notifier = get(),
            log = get(),
        )
    }
}