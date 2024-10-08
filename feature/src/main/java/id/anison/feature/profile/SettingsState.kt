package id.anison.feature.profile

import id.anison.data.model.AppSettings
import id.anison.data.model.AppTheme


data class SettingsState(
    val currentAppSettings: AppSettings?/*StateFlow<AppSettings?>*/ = null,//MutableStateFlow(AppSettings()),
    val theme: AppTheme = AppTheme.FOLLOW_SYSTEM,
    val isDynamicColor: Boolean? = null,// = false,
    val isBlackTheme: Boolean? = null,// = false,
    val countColumnsInRow: Int/*?*/ = 3,
    val countColumnsInRowLandscape: Int/*?*/ = 6,
    val isLoading: Boolean = true,
){
    companion object {
        val None = SettingsState()
    }
}