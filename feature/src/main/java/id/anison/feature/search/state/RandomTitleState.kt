package id.anison.feature.search.state

import id.anison.feature.model.AnimeUI
import id.anison.feature.common.DataState

data class RandomTitleState(
    val maxTitles: Int = 0,
    val animeCount: Int = 0,
    val animeIDs: DataState<List<Long>>? = DataState.Loading,
    val randomAnime: DataState<AnimeUI> = DataState.Loading,
){
    companion object {
        val None = RandomTitleState()
    }
}