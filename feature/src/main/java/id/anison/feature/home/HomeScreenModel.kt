package id.anison.feature.home

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ConnectivityRepo
import id.anison.data.ReqRes
import id.anison.data.anime.AnimeRepo
import id.anison.feature.anime.model.AnimeUI
import id.anison.feature.anime.model.toAnimeUI
import id.anison.feature.common.Chip
import id.anison.feature.common.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val repo: AnimeRepo,
    private val connectivityRepo: ConnectivityRepo,
) : StateScreenModel<HomeState>(HomeState()) {

    private val _state = MutableStateFlow(HomeState())
    val animeState: StateFlow<HomeState> = _state.asStateFlow()

    val pagingAnimeState = repo.getAllWithPagination()
        .map {
            it.map { animeDto -> animeDto.toAnimeUI() }
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = PagingData.empty()
        )
        .cachedIn(screenModelScope)

    val isOnline = connectivityRepo.isConnected
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = true,
        )

    companion object {
        const val TAG = "HomeScreenModel"
    }

    init {
        Log.e(TAG, "Init")
        if (_state.value.currentAnimeState is DataState.Loading) {
            getCurrentAnimeList()
        }
    }

    fun observeConnectivity() {
        screenModelScope.launch {
            isOnline.collect { isConnected ->
                _state.update { it.copy(isOnline = isConnected) }
                if (isConnected) {
                    refreshTitles()
                }
            }
        }
    }

    fun refreshTitles() {
        if (_state.value.currentAnimeState !is DataState.Loading) {
            getCurrentAnimeList()
        }
    }

    fun getCurrentAnimeList() = screenModelScope.launch {
        Log.e(TAG, "fetching curr")
        repo.getCurrentAnime().collectLatest { state ->
            when (val animeData = state) {
                is ReqRes.Error -> {
                    _state.update { state ->
                        state.copy(currentAnimeState = DataState.Error(animeData.error))
                    }
                }

                is ReqRes.InProgress -> {
                    _state.update { state ->
                        state.copy(currentAnimeState = DataState.Loading)
                    }
                }

                is ReqRes.Success -> {
                    delay(1000)
                    _state.update { state ->
                        state.copy(
                            currentAnimeState = DataState.Success(animeData.data.map { it.toAnimeUI() })
                        )
                    }
                }
            }
        }
    }

    fun setSelectedFilterChip(value: String) {
        _state.update { state -> state.copy(selectedChipFilter = value) }
    }
}

data class HomeState(
    val currentAnimeState: DataState<List<AnimeUI>> = DataState.Loading,
    val isOnline: Boolean = true,
    val selectedChipFilter: String = Chip.ALL.title,
)