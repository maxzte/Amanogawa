package id.anison.amanogawa.core.navigation

import cafe.adriel.voyager.core.screen.Screen
import id.anison.amanogawa.core.model.FilterUI

sealed interface Destination {
    data class AnimeDetailsScreenDestination(val animeId: Long): Destination
    data class FilteredSearchScreenDestination(val filterName: String): Destination
    data class ExtendedSearchResultScreenDestination(val filter: FilterUI): Destination
    data object ExtendedSearchScreenDestination : Destination
    data object RandomAnimeScreenDestination: Destination
    data object FavoriteScreenDestination: Destination
    data object WatchLaterScreenDestination: Destination
}

interface DestinationResolver {
    fun resolve(destination: Destination): Screen
}