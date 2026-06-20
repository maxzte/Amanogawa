package id.anison.amanogawa.core.domain.repository

import id.anison.amanogawa.core.domain.model.AppSettings
import id.anison.amanogawa.core.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepo {

    val settings: Flow<AppSettings>

    suspend fun saveTheme(theme: AppTheme)

    suspend fun saveDynamicColor(isDynamic: Boolean)

    suspend fun saveBlackTheme(isBlack: Boolean)

    suspend fun saveCountColumnsInRow(count: Int)

    suspend fun saveCountColumnsInRowLandscape(count: Int)

    suspend fun saveCountEpisodesInRow(count: Int)

    suspend fun saveCountEpisodesInRowLandscape(count: Int)
}
