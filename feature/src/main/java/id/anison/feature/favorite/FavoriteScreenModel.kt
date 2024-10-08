package id.anison.feature.favorite

import androidx.work.WorkManager
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ReqRes
import id.anison.data.repository.AmanogawaRepository
import id.anison.feature.common.DataState
import id.anison.feature.model.FavoriteUI
import id.anison.feature.model.toDataFavorite
import id.anison.feature.model.toFavoriteUI
import id.anison.feature.work.NotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteScreenModel(
    private val repository: AmanogawaRepository,
    private val workManager: WorkManager,
) : StateScreenModel<FavoriteState>(FavoriteState.None) {
    private val _favoriteState = MutableStateFlow(FavoriteState.None)
    val favoriteState: StateFlow<FavoriteState> = _favoriteState.asStateFlow()

    init {
        getFavoritesList()
    }

    fun removeFromFavorite(favorite: FavoriteUI) {
        screenModelScope.launch {
            repository.removeFromFavorite(favorite.toDataFavorite())
            NotificationWorker.stopWork(workManager, favorite.en_jap_name)
        }
    }

    fun clearFavorite() {
        screenModelScope.launch {
            repository.clearFavoriteList()
            NotificationWorker.stopAllWorks(workManager)
        }
    }

    private fun getFavoritesList() {
        screenModelScope.launch {
            repository.getFavorites().collect { res ->
                when (res) {
                    is ReqRes.Error -> {
                        _favoriteState.value = _favoriteState.value.copy(
                            favoritesList = res.error?.cause?.let { DataState.Error(it) }
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
}