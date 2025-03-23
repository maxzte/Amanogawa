package id.anison.feature.search.screenmodel

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ReqRes
import id.anison.data.search.SearchRepo
import id.anison.feature.anime.model.AnimeDetailsUI
import id.anison.feature.anime.model.toAnimeUI
import id.anison.feature.common.DataState
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class RandomTitleScreenModel(
    private val searchRepo: SearchRepo,
) : StateScreenModel<RandomTitleState>(RandomTitleState()) {

    private val _randomTitleState = MutableStateFlow(RandomTitleState())
    val randomTitleState: StateFlow<RandomTitleState> = _randomTitleState.asStateFlow()

    init {
        getAnimeIDs()
    }

    private fun getAnimeIDs() {
        val animeIDs = when (val state = _randomTitleState.value.animeIDs) {
            is DataState.Success -> {
                state.data
            }

            else -> {
                null
            }
        }
        if (animeIDs == null) {
            screenModelScope.launch {
                val existingAnimeCount = searchRepo.getExistingAnimeCount()
                try {
                    withTimeout(10000L) {
                        when (val res = searchRepo.getAnimeIDs()) {
                            is ReqRes.Error -> {
                                _randomTitleState.update { it.copy(animeIDs = DataState.Error(res.error)) }
                            }

                            is ReqRes.InProgress -> {
                                _randomTitleState.update { it.copy(animeIDs = DataState.Loading) }
                            }

                            is ReqRes.Success -> {
                                _randomTitleState.update {
                                    it.copy(
                                        animeIDs = DataState.Success(res.data),
                                        maxTitles = res.data.size,
                                        animeCount = existingAnimeCount,
                                    )
                                }
                                getRandomAnime(res.data)
                            }
                        }
                    }
                } catch (e: TimeoutCancellationException) {
                    _randomTitleState.update { it.copy(animeIDs = DataState.Error(e.cause)) }
                }
            }
        } else {
            getRandomAnime(animeIDs)
        }
    }

    fun getRandomAnime(animeIDs: List<Long>) {
        screenModelScope.launch {
            searchRepo.getRandomAnime(
                ids = animeIDs,
                count = _randomTitleState.value.animeCount
            ).collectLatest { randTitle ->
                when (randTitle) {
                    is ReqRes.Error -> {
                        _randomTitleState.update {
                            it.copy(randomAnime = DataState.Error(randTitle.error))
                        }
                    }

                    is ReqRes.InProgress -> {
                        _randomTitleState.update {
                            it.copy(randomAnime = DataState.Loading)
                        }
                    }

                    is ReqRes.Success -> {
                        if (isLast()) {
                            _randomTitleState.update { it.copy(animeCount = 0) }
                        }
                        _randomTitleState.update {
                            it.copy(
                                randomAnime = DataState.Success(randTitle.data.toAnimeUI()),
                                animeCount = _randomTitleState.value.animeCount + 1,
                            )
                        }
                    }
                }
            }
        }
    }

    fun refresh() {
        getAnimeIDs()
    }

    fun isLast(): Boolean {
        return _randomTitleState.value.maxTitles == _randomTitleState.value.animeCount
    }
}

data class RandomTitleState(
    val maxTitles: Int = 0,
    val animeCount: Int = 0,
    val animeIDs: DataState<List<Long>> = DataState.Loading,
    val randomAnime: DataState<AnimeDetailsUI> = DataState.Loading,
)