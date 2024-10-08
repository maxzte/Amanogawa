package id.anison.feature.search.event

interface ExtendedSearchTitlesScreenEvents {
    data object PopBack : ExtendedSearchTitlesScreenEvents
    data class PushTitle(val id: Long) : ExtendedSearchTitlesScreenEvents
    data object Refresh : ExtendedSearchTitlesScreenEvents
}