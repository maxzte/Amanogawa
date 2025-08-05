package id.anison.amanogawa.core.ui.common

import androidx.compose.runtime.compositionLocalOf

data class AppSettingsUI(
    val countColumnsInRow: Int = 3,
    val countColumnsInRowLandscape: Int = 6,
    val countEpisodesInRow: Int = 1,
    val countEpisodesInRowLandscape: Int = 3,
)

val LocalAppSettings = compositionLocalOf { AppSettingsUI() }