package id.anison.feature.home

import android.util.Log
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.repository.AmanogawaRepository
import id.anison.data.repository.ConnectivityRepository
import id.anison.feature.model.toCurrentTitleUI
import id.anison.feature.model.toTitleUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val repository: AmanogawaRepository,
    private val connectivityRepository: ConnectivityRepository,
) : StateScreenModel<HomeState>(HomeState.None) {
    private val _state = MutableStateFlow(HomeState.None)
    val animeState: StateFlow<HomeState> = _state.asStateFlow()
    val isOnline = connectivityRepository.isConnected
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = true,
        )

    //var currentPosition: Int? = _state.value.titlesState.collectAsLazyPagingItems().peek()//null
    private var currentPosition: Int? = null
    private var currentPositionCurrent: Int? = null

    init {
        getTitles()
        //observeConnectivity()
    }

    fun updateScrollPosition(newPosition: Int, newPositionForCurrent: Int) {
        Log.d("Home", "updScrollPos is: $newPosition and curr is: $newPosition")
        currentPosition = newPosition
        currentPositionCurrent = newPositionForCurrent
    }

    fun observeConnectivity() {
        screenModelScope.launch {
            isOnline.collect { isConnected ->
                _state.update { it.copy(isOnline = isConnected) }
                if (isConnected) {
//                    getTitles()
//                    getCurrentTitles()
                    refreshTitles()
                    refreshCurrentTitles()
                }
            }
        }
    }

    fun refreshTitles(): Boolean {
        _state.value = _state.value.copy(isTitlesLoaded = false)
        getTitles()
        return _state.value.isTitlesLoaded
    }

    fun refreshCurrentTitles() {
        _state.value = _state.value.copy(isCurrentTitlesLoaded = false)
        getCurrentTitles()
    }

    private fun getTitles() {
        screenModelScope.launch {
            if (!_state.value.isTitlesLoaded) {
                val titlesState = repository.getAll().map {
                    it.map { dataTitle -> dataTitle.toTitleUI() }
                }
                    .cachedIn(screenModelScope)
                    .stateIn(screenModelScope, SharingStarted.Lazily, PagingData.empty())

                _state.value = _state.value.copy(isTitlesLoaded = true, titlesState = titlesState)
            }
        }
    }

    fun getCurrentTitles() {
        screenModelScope.launch {
            if (!_state.value.isCurrentTitlesLoaded) {
                Log.d("Home", "currentTitlesCurrent is $currentPositionCurrent")
                val currentTitlesState = repository.getAllCurrent()
                    .map { pagingData -> pagingData.map { it.toCurrentTitleUI() } }
                    .cachedIn(screenModelScope)
                    .stateIn(screenModelScope, SharingStarted.Lazily, PagingData.empty())

                _state.value = _state.value.copy(
                    isCurrentTitlesLoaded = true,
                    currentTitlesState = currentTitlesState
                )
            }
        }
    }

    /*private fun getTitles() {
        screenModelScope.launch {
            if (!_state.value.isTitlesLoaded) {
                Log.d("Home", "currentTitles is $currentPosition")
                val titlesState: StateFlow<PagingData<TitleUI>> = repository.getAll(pagingConfig, currentPositionCurrent)
                    .map { it.map { dataTitle -> dataTitle.toTitleUI() } }
                    .cachedIn(screenModelScope)
                    .stateIn(screenModelScope, SharingStarted.Lazily, PagingData.empty())

                _state.value = _state.value.copy(isTitlesLoaded = true, titlesState = titlesState)
            }
        }
    }*/
}