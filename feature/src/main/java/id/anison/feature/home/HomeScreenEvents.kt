package id.anison.feature.home

interface HomeScreenEvents {
    data class PushAnime(val id: Long, val schedule: String): HomeScreenEvents
    data object GetCurrentTitles: HomeScreenEvents
    data object OnRefresh: HomeScreenEvents
    data object OnCurrentTitlesRefresh: HomeScreenEvents
    data class ObserveConnection(val index: Int, val currIndex: Int): HomeScreenEvents
}