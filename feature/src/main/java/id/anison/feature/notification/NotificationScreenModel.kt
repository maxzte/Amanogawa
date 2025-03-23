package id.anison.feature.notification

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ReqRes
import id.anison.data.anime.AnimeRepo
import id.anison.data.favorite.FavoriteRepo
import id.anison.feature.anime.model.AnimeUI
import id.anison.feature.common.DataState
import id.anison.feature.favorite.FavoriteUI
import id.anison.feature.favorite.toFavoriteUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class NotificationScreenModel(
    private val favoriteRepo: FavoriteRepo,
    private val animeRepo: AnimeRepo,
) : StateScreenModel<NotificationState>(NotificationState()) {

    val notificationState = MutableStateFlow(NotificationState())


    init {
        getNewEpFromFavorites()
    }

    private fun getNewEpFromFavorites() {
        screenModelScope.launch {
            favoriteRepo.observeNewEpFromFavorites().collectLatest {
                when (val favorites = it) {
                    is ReqRes.Error -> {
                        notificationState.update {
                            it.copy(
                                notificationsFromFavorites = DataState.Error(favorites.error)
                            )
                        }
                    }

                    is ReqRes.InProgress -> {
                        notificationState.update {
                            it.copy(
                                notificationsFromFavorites = DataState.Loading
                            )
                        }
                    }

                    is ReqRes.Success -> {
                        notificationState.update {
                            it.copy(
                                notificationsFromFavorites = DataState.Success(favorites.data.map { it.toFavoriteUI() }),
                                notificationFromCurrentTitles = DataState.Success(data = emptyList())
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NotificationState(
    val notificationsFromFavorites: DataState<List<FavoriteUI>> = DataState.Loading,
    val notificationFromCurrentTitles: DataState<List<AnimeUI>> = DataState.Loading,
)