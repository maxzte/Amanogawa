package id.anison.amanogawa.core.work.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("id.anison.amanogawa.core.work")
class WorkerModule

/*
import id.anison.amanogawa.core.work.CacheCleanUpWorker
import id.anison.amanogawa.core.work.NotificationWorker
import org.koin.dsl.module
import org.koin.plugin.module.dsl.worker

val workerModule = module {
    worker<CacheCleanUpWorker>()
    worker<NotificationWorker>()
}
*/
