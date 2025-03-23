package id.anison.feature.favorite


interface FavoritesScreenEvents {
    data object PopBack: FavoritesScreenEvents
    data class RemoveFromFavorite(val favorite: FavoriteUI): FavoritesScreenEvents
    data object ClearFavorite: FavoritesScreenEvents
    data class OnFavoriteClick(val favoriteId: Long): FavoritesScreenEvents
}