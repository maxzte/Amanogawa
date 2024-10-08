package id.anison.feature.anime

import id.anison.feature.model.AnimeUI
import id.anison.feature.model.FilterUI

interface AnimeScreenEvents {
    data object PopBack: AnimeScreenEvents
    data class PushAnimeScreen(val id: Long): AnimeScreenEvents
    data class AddToFavorite(val anime: AnimeUI): AnimeScreenEvents
    data class RemoveFromFavorite(val anime: AnimeUI): AnimeScreenEvents
    data class OnLinkClick(val filter: FilterUI, val filterName: String): AnimeScreenEvents
    data class Refresh(val id: Long): AnimeScreenEvents
}
