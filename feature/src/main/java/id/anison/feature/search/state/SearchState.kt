package id.anison.feature.search.state

import id.anison.feature.model.FoundAnimeUI
import id.anison.feature.common.DataState

data class SearchState(
    val foundTitles: DataState<List<FoundAnimeUI>>? = null,
    val searchHistory: DataState<List<FoundAnimeUI>>? = null,
    val isAll: Boolean = false,
){
    companion object {
        val None = SearchState()
    }
}