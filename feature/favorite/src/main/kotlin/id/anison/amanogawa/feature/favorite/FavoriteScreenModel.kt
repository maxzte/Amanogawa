package id.anison.amanogawa.feature.favorite

import androidx.compose.foundation.lazy.grid.LazyGridState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.common.SingleEventHandler
import id.anison.amanogawa.core.common.mapDataState
import id.anison.amanogawa.core.data.favorite.FavoriteRepo
import id.anison.amanogawa.core.domain.toFavorite
import id.anison.amanogawa.core.domain.toFavoriteUI
import id.anison.amanogawa.core.model.FavoriteUI
import id.anison.amanogawa.core.navigation.SharedScreen.AnimeDetails
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteScreenModel(
    private val favoriteRepo: FavoriteRepo,
    private val log: Logger,
) : ScreenModel {
    private val singleEventHandler = SingleEventHandler()
    val lazyGridState = LazyGridState()
    val favoriteState: StateFlow<DataState<List<FavoriteUI>>> = favoriteRepo.getFavorites()
        .map { res ->
            res.mapDataState { it.map { favorite -> favorite.toFavoriteUI() } }
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DataState.Loading
        )

    private val _uiNavigationEvent = MutableSharedFlow<FavoriteNavigationEvent>()
    val navigationEvent = _uiNavigationEvent.asSharedFlow()

    fun onEvent(event: FavoritesScreenEvents) {
        log.d(TAG, "event is: $event")
        when (event) {
            is FavoritesScreenEvents.OnFavoriteClick -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _uiNavigationEvent.emit(
                            value = FavoriteNavigationEvent.NavigateToDetails(
                                animeId = event.favoriteId
                            )
                        )
                    }
                }
            }

            is FavoritesScreenEvents.PopBack -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _uiNavigationEvent.emit(value = FavoriteNavigationEvent.NavigateToBack)
                    }
                }
            }

            is FavoritesScreenEvents.ClearFavorite -> {
                if (singleEventHandler.canTrigger()) {
                    clearFavorite()
                }
            }

            is FavoritesScreenEvents.RemoveFromFavorite -> {
                if (singleEventHandler.canTrigger()) {
                    removeFromFavorite(favorite = event.favorite)
                }
            }
        }
    }

    private fun clearFavorite() = screenModelScope.launch {
        favoriteRepo.clearFavorites()
    }

    private fun removeFromFavorite(favorite: FavoriteUI) = screenModelScope.launch {
        favoriteRepo.removeFromFavorite(favorite.toFavorite())
    }

    companion object {
        private const val TAG = "FavoriteSM"
    }

}

sealed interface FavoritesScreenEvents {
    object PopBack : FavoritesScreenEvents
    data class RemoveFromFavorite(val favorite: FavoriteUI) : FavoritesScreenEvents
    object ClearFavorite : FavoritesScreenEvents
    data class OnFavoriteClick(val favoriteId: Long) : FavoritesScreenEvents
}

sealed interface FavoriteNavigationEvent {
    object NavigateToBack : FavoriteNavigationEvent
    data class NavigateToDetails(val animeId: Long) : FavoriteNavigationEvent
}

internal fun FavoriteNavigationEvent.toScreen(): Screen? = when (this) {
    is FavoriteNavigationEvent.NavigateToDetails -> ScreenRegistry.get(AnimeDetails(animeId = this.animeId))
    is FavoriteNavigationEvent.NavigateToBack -> null
}
