package id.anison.amanogawa.core.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider
import id.anison.amanogawa.core.model.FilterTypeUI

sealed class SharedScreen : ScreenProvider {

    data class AnimeDetails(val animeId: Long) : SharedScreen()

    /**
     * Nav to FilterResultScreen
     * @param filterName
     */
    data class SearchResult(
        val filterId: Long,
        val filterName: String,
        val filterType: FilterTypeUI
    ) : SharedScreen()

    object Favorites : SharedScreen()

}