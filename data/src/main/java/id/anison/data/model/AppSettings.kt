package id.anison.data.model

data class AppSettings(
    val theme: AppTheme = AppTheme.FOLLOW_SYSTEM,
    val isDynamicColors: Boolean = false,
    val isBlackTheme: Boolean = false,
    val countColumnsInRow: Int = 3,
    val countColumnsInRowLandscape: Int = 6,
)