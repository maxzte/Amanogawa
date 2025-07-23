package id.anison.amanogawa.feature.home

sealed interface HomeScreenEvents {
    data class PushAnime(val animeId: Long) : HomeScreenEvents
    data class SetChipFilter(val chipTitle: String) : HomeScreenEvents
    data object OnRefresh : HomeScreenEvents
}