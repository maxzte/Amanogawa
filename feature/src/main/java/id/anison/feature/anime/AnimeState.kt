package id.anison.feature.anime

import id.anison.feature.model.AnimeUI
import id.anison.feature.model.EpisodeUI
import id.anison.feature.common.DataState

data class AnimeState(
    val anime: DataState<AnimeUI>? = DataState.Loading,
    val episodes: DataState<List<EpisodeUI>>? = DataState.Loading,
    val isFavorite: Boolean = false,
    val isLoaded: Boolean = false,
    val isEpisodesLoaded: Boolean = false,
){
    companion object {
        val None = AnimeState()
    }
}
