package id.anison.amanogawa.core.data.di

import id.anison.amanogawa.core.database.di.DatabaseModule
import id.anison.amanogawa.core.datastore.di.DatastoreModule
import id.anison.amanogawa.core.network.di.NetworkModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [DatabaseModule::class, NetworkModule::class, DatastoreModule::class])
@ComponentScan("id.anison.amanogawa.core.data")
class DataModule
