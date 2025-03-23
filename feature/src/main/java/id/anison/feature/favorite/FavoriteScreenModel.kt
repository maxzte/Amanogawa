package id.anison.feature.favorite

import androidx.work.WorkManager
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ReqRes
import id.anison.data.favorite.FavoriteRepo
import id.anison.feature.common.DataState
import id.anison.feature.work.NotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteScreenModel(
    private val favoriteRepo: FavoriteRepo,
    private val workManager: WorkManager,
) : StateScreenModel<FavoriteState>(FavoriteState()) {

    private val _favoriteState = MutableStateFlow(FavoriteState())
    val favoriteState: StateFlow<FavoriteState> = _favoriteState.asStateFlow()

    init {
        getFavoritesList()
    }

    private fun getFavoritesList() {
        screenModelScope.launch {
            favoriteRepo.getFavorites().collectLatest { res ->
                when (res) {
                    is ReqRes.Error -> {
                        _favoriteState.value = _favoriteState.value.copy(
                            favoritesList = DataState.Error(res.error)
                        )
                    }

                    is ReqRes.InProgress -> {
                        _favoriteState.value = _favoriteState.value.copy(
                            favoritesList = DataState.Loading
                        )
                    }

                    is ReqRes.Success -> {
                        _favoriteState.value = _favoriteState.value.copy(
                            favoritesList = DataState.Success(res.data.map { it.toFavoriteUI() })
                        )
                    }
                }
            }
        }
    }

    fun removeFromFavorite(favorite: FavoriteUI) {
        screenModelScope.launch {
            favoriteRepo.removeGenresByFavoriteId(favorite.favoriteId)
            favoriteRepo.removeFromFavorite(favorite.toFavoriteDto())
            NotificationWorker.stopWork(workManager, favorite.enJpName)
        }
    }

    fun clearFavorite() {
        screenModelScope.launch {
            favoriteRepo.clearFavoriteList()
            NotificationWorker.stopAllWorks(workManager)
        }
    }
}

data class FavoriteState(
    val favoritesList: DataState<List<FavoriteUI>> = DataState.Loading,
    val isFavorite: Boolean = false,
)