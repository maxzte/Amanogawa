package id.anison.data.settings

import androidx.paging.PagingConfig

data class AppSettings(
    val theme: AppTheme = AppTheme.FOLLOW_SYSTEM,
    val isDynamicColors: Boolean = false,
    val isBlackTheme: Boolean = false,
    val countColumnsInRow: Int = 3,
    val countColumnsInRowLandscape: Int = 6,
)

enum class AppTheme {
    LIGHT,
    DARK,
    FOLLOW_SYSTEM,
}

val config: PagingConfig = PagingConfig(
    pageSize = 10,
    prefetchDistance = 10 / 2,
    enablePlaceholders = false,
)