package id.anison.amanogawa.feature.anime

import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.FilterTypeUI


interface AnimeDetailsScreenEvents {
    data object PopBack : AnimeDetailsScreenEvents
    data object ScrollToTop : AnimeDetailsScreenEvents
    data class PushAnimeDetailsScreen(val animeId: Long) : AnimeDetailsScreenEvents
    data class ToggleFavorite(val anime: AnimeDetailsUI) : AnimeDetailsScreenEvents
    data class OnLinkClick(
        val filterId: Long?,
        val filterName: String,
        val filterType: FilterTypeUI
    ) : AnimeDetailsScreenEvents

    data object Refresh : AnimeDetailsScreenEvents
}
