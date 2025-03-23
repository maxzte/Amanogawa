package id.anison.feature.profile.event

import id.anison.data.settings.AppTheme

interface AppearanceScreenEvents {
    data object PopBack: AppearanceScreenEvents
    data class SaveTheme(val theme: AppTheme): AppearanceScreenEvents
    data class SaveDynamicColors(val isDynamicColors: Boolean): AppearanceScreenEvents
    data class SaveBlackTheme(val isBlackTheme: Boolean): AppearanceScreenEvents
    data class SaveColumnsInRow(val columns: Int): AppearanceScreenEvents
    data class SaveColumnsInRowLandscape(val columns: Int): AppearanceScreenEvents
}