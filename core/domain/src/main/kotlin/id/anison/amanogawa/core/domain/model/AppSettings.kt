package id.anison.amanogawa.core.domain.model

data class AppSettings(
    val theme: AppTheme = AppTheme.FOLLOW_SYSTEM,
    val isDynamicColors: Boolean = false,
    val isBlackTheme: Boolean = false,
)

enum class AppTheme {
    LIGHT,
    DARK,
    FOLLOW_SYSTEM,
}
