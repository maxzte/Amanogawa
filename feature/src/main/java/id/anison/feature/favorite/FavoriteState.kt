package id.anison.feature.favorite

import id.anison.feature.model.FavoriteUI
import id.anison.feature.common.DataState


data class FavoriteState(
    val favoritesList: DataState<List<FavoriteUI>>? = null,
    val isFavorite: Boolean = false,
) {
    companion object {
        val None = FavoriteState()
    }
}
