package id.anison.amanogawa.core.domain.model

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
