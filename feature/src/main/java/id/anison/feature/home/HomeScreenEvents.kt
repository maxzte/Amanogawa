package id.anison.feature.home

sealed interface HomeScreenEvents {
    data class PushAnime(val animeId: Long) : HomeScreenEvents
    data class SetChipFilter(val chipTitle: String) : HomeScreenEvents
    data object OnRefresh : HomeScreenEvents
    data class ObserveConnection(val index: Int, val currIndex: Int) : HomeScreenEvents
}