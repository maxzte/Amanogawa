package id.anison.feature.home

import androidx.paging.PagingData
import id.anison.feature.model.CurrentTitleUI
import id.anison.feature.model.TitleUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HomeState(
    val titlesState: StateFlow<PagingData<TitleUI>> = MutableStateFlow(PagingData.empty()),
    val currentTitlesState: StateFlow<PagingData<CurrentTitleUI>> = MutableStateFlow(PagingData.empty()),
    val isTitlesLoaded: Boolean = false,
    val isCurrentTitlesLoaded: Boolean = false,
    val isOnline: Boolean = true,
){
    companion object{
        val None = HomeState()
    }
}

