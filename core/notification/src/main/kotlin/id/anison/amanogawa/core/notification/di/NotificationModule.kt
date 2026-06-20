package id.anison.amanogawa.core.notification.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("id.anison.amanogawa.core.notification")
class NotificationModule

/*
import id.anison.amanogawa.core.notification.NewEpisodesNotifier
import id.anison.amanogawa.core.notification.Notifier
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val notificationModule = module {
    single<NewEpisodesNotifier>() bind Notifier::class
}
*/
