package id.anison.amanogawa.core.data.settings

import androidx.paging.PagingConfig

data class AppSettings(
    val theme: AppTheme = AppTheme.FOLLOW_SYSTEM,
    val isDynamicColors: Boolean = false,
    val isBlackTheme: Boolean = false,
    val countColumnsInRow: Int = 3,
    val countColumnsInRowLandscape: Int = 6,
    val countEpisodesInRow: Int = 1,
    val countEpisodesInRowLandscape: Int = 3,
)

enum class AppTheme {
    LIGHT,
    DARK,
    FOLLOW_SYSTEM,
}

object PageConfig {
    const val PAGE_SIZE = 10
    const val PREFETCH_DISTANCE = PAGE_SIZE * 2
    const val MAX_SIZE = PAGE_SIZE + (PREFETCH_DISTANCE * 2)
}

val config: PagingConfig = PagingConfig(
    pageSize = PageConfig.PAGE_SIZE,
    prefetchDistance = PageConfig.PREFETCH_DISTANCE,
)