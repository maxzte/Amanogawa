package id.anison.amanogawa.feature.home

import cafe.adriel.voyager.core.model.StateScreenModel
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.data.anime.AnimeRepo
import id.anison.amanogawa.core.data.favorite.FavoriteRepo
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.model.FavoriteUI
import kotlinx.coroutines.flow.MutableStateFlow


class NotificationScreenModel(
    private val favoriteRepo: FavoriteRepo,
    private val animeRepo: AnimeRepo,
) : StateScreenModel<NotificationState>(NotificationState()) {

    val notificationState = MutableStateFlow(NotificationState())


    init {
        getNewEpFromFavorites()
    }

    private fun getNewEpFromFavorites() {
//        screenModelScope.launch {
//            favoriteRepo.observeNewEpFromFavorites().collectLatest {
//                when (val favorites = it) {
//                    is ReqRes.Error -> {
//                        notificationState.update {
//                            it.copy(
//                                notificationsFromFavorites = DataState.Error(favorites.error)
//                            )
//                        }
//                    }
//
//                    is ReqRes.InProgress -> {
//                        notificationState.update {
//                            it.copy(
//                                notificationsFromFavorites = DataState.Loading
//                            )
//                        }
//                    }
//
//                    is ReqRes.Success -> {
//                        notificationState.update {
//                            it.copy(
//                                notificationsFromFavorites = DataState.Success(favorites.data.map { it.toFavoriteUI() }),
//                                notificationFromCurrentTitles = DataState.Success(data = emptyList())
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }
}

data class NotificationState(
    val notificationsFromFavorites: DataState<List<FavoriteUI>> = DataState.Loading,
    val notificationFromCurrentTitles: DataState<List<AnimeUI>> = DataState.Loading,
)