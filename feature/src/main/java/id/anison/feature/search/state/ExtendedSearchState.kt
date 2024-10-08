package id.anison.feature.search.state

import androidx.paging.PagingData
import id.anison.feature.model.FilterUI
import id.anison.feature.model.TitleUI
import id.anison.feature.common.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ExtendedSearchState(
    val filterOptions: DataState<FilterUI>? = DataState.Loading,
    val filter: FilterUI = FilterUI(),
    val filteredTitles: StateFlow<PagingData<TitleUI>> = MutableStateFlow(PagingData.empty()),
    val titlesByLink: StateFlow<PagingData<TitleUI>> = MutableStateFlow(PagingData.empty()),
    val isOnline: Boolean = true,
){
    companion object {
        val None = ExtendedSearchState()
    }
}