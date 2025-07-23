package id.anison.amanogawa.core.ui.common

import androidx.compose.runtime.Composable


@Composable
fun columnsInRow(): Int {
    val appSettings = LocalAppSettings.current
    val isLandscape = isLandscape()
    return if (!isLandscape) {
        appSettings.countColumnsInRow
    } else {
        appSettings.countColumnsInRowLandscape
    }
}

@Composable
fun episodesInRow(): Int {
    val appSettings = LocalAppSettings.current
    val isLandscape = isLandscape()
    return if (isLandscape) {
        appSettings.countEpisodesInRowLandscape
    } else {
        appSettings.countEpisodesInRow
    }
}
