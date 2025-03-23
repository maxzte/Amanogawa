package id.anison.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

val Context.createDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepo(context: Context) {
    private val dataStore = context.createDataStore

    companion object {
        private val THEME_KEY = intPreferencesKey("theme")
        private val IS_DYNAMIC_COLOR_THEME = booleanPreferencesKey("is_dynamic_theme")
        private val IS_BLACK_THEME_KEY = booleanPreferencesKey("is_black_theme")
        private val COUNT_COLUMNS_IN_ROW_KEY = intPreferencesKey("count_columns_in_row")
        private val COUNT_COLUMNS_IN_ROW_LANDSCAPE_KEY =
            intPreferencesKey("count_columns_in_row_landscape")
    }

    val settings: Flow<AppSettings> = dataStore.data
        .catch {
            if (it is IOException) emit(emptyPreferences()) else throw it
        }
        .map { preferences ->
            AppSettings(
                theme = AppTheme.entries[preferences[THEME_KEY] ?: AppTheme.FOLLOW_SYSTEM.ordinal],
                isDynamicColors = preferences[IS_DYNAMIC_COLOR_THEME] ?: false,
                isBlackTheme = preferences[IS_BLACK_THEME_KEY] ?: false,
                countColumnsInRow = preferences[COUNT_COLUMNS_IN_ROW_KEY] ?: 3,
                countColumnsInRowLandscape = preferences[COUNT_COLUMNS_IN_ROW_LANDSCAPE_KEY] ?: 6,
            )
        }.distinctUntilChanged()

    val theme = dataStore.data.map {
        AppTheme.entries[it[THEME_KEY] ?: AppTheme.FOLLOW_SYSTEM.ordinal]
    }

    suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.ordinal
        }
    }

    suspend fun saveDynamicColor(isDynamic: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DYNAMIC_COLOR_THEME] = isDynamic
        }
    }

    suspend fun saveBlackTheme(isBlack: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_BLACK_THEME_KEY] = isBlack
        }
    }

    suspend fun saveCountColumnsInRow(count: Int) {
        dataStore.edit { preferences ->
            if (preferences[COUNT_COLUMNS_IN_ROW_KEY] != count)
                preferences[COUNT_COLUMNS_IN_ROW_KEY] = count
        }
    }

    suspend fun saveCountColumnsInRowLandscape(count: Int) {
        dataStore.edit { preferences ->
            if (preferences[COUNT_COLUMNS_IN_ROW_LANDSCAPE_KEY] != count)
                preferences[COUNT_COLUMNS_IN_ROW_LANDSCAPE_KEY] = count
        }
    }
}