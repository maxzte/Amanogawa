package id.anison.amanogawa.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import id.anison.amanogawa.core.domain.model.AppSettings
import id.anison.amanogawa.core.domain.model.AppTheme
import id.anison.amanogawa.core.domain.repository.SettingsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class SettingsRepoImpl(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepo {

    override val settings: Flow<AppSettings> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            AppSettings(
                theme = AppTheme.entries[preferences[THEME_KEY] ?: AppTheme.FOLLOW_SYSTEM.ordinal],
                isDynamicColors = preferences[IS_DYNAMIC_COLOR_THEME] ?: false,
                isBlackTheme = preferences[IS_BLACK_THEME_KEY] ?: false,
            )
        }.distinctUntilChanged()

    override suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.ordinal
        }
    }

    override suspend fun saveDynamicColor(isDynamic: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DYNAMIC_COLOR_THEME] = isDynamic
        }
    }

    override suspend fun saveBlackTheme(isBlack: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_BLACK_THEME_KEY] = isBlack
        }
    }

    companion object {
        private const val TAG = "SettingsRepo"

        private val THEME_KEY = intPreferencesKey("theme")
        private val IS_DYNAMIC_COLOR_THEME = booleanPreferencesKey("is_dynamic_theme")
        private val IS_BLACK_THEME_KEY = booleanPreferencesKey("is_black_theme")
    }
}
