package id.anison.amanogawa.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "amanogawa_settings")

@Module
@ComponentScan("id.anison.amanogawa.core.datastore")
class DatastoreModule {

    @Single
    fun provideDataStore(context: Context): DataStore<Preferences> = context.dataStore
}
