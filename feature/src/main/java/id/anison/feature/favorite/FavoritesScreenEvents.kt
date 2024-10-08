package id.anison.feature.favorite

import id.anison.feature.model.FavoriteUI

interface FavoritesScreenEvents {
    data object PopBack: FavoritesScreenEvents
    data class RemoveFromFavorite(val favorite: FavoriteUI): FavoritesScreenEvents
    data object ClearFavorite: FavoritesScreenEvents
    data class OnFavoriteClick(val id: Long): FavoritesScreenEvents
}