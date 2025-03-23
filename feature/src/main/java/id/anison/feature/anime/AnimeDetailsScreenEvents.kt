package id.anison.feature.anime

import id.anison.feature.anime.model.AnimeDetailsUI
import id.anison.feature.search.model.FilterUI


interface AnimeDetailsScreenEvents {
    data object PopBack: AnimeDetailsScreenEvents
    data class PushAnimeDetailsScreen(val animeId: Long): AnimeDetailsScreenEvents
    data class ToggleFavorite(val anime: AnimeDetailsUI): AnimeDetailsScreenEvents
    data class OnLinkClick(val filter: FilterUI, val filterName: String): AnimeDetailsScreenEvents
    data class Refresh(val animeId: Long): AnimeDetailsScreenEvents
}
