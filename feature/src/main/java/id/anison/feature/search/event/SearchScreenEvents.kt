package id.anison.feature.search.event

import id.anison.feature.search.model.FilterUI
import id.anison.feature.search.model.FoundAnimeUI

sealed interface SearchScreenEvents {
    data class OnAnimeClick(val animeId: Long, val anime: FoundAnimeUI) : SearchScreenEvents
    data class OnFilterClick(val filter: FilterUI, val filterName: String) : SearchScreenEvents
    data class OnSearchQueryChange(val query: String) : SearchScreenEvents
    object OnRandomButtonClick : SearchScreenEvents
    object OnExtendedButtonClick : SearchScreenEvents
    object OnDeleteAllButtonClick : SearchScreenEvents
    data class OnDeleteAnimeFromHistory(val anime: FoundAnimeUI) : SearchScreenEvents
}