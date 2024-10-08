package id.anison.feature.search.screenmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ReqRes
import id.anison.data.repository.AmanogawaRepository
import id.anison.feature.common.DataState
import id.anison.feature.model.toAnimeUI
import id.anison.feature.search.state.RandomTitleState
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class RandomTitleScreenModel(
    private val repository: AmanogawaRepository,
) : StateScreenModel<RandomTitleState>(RandomTitleState.None) {
    private val _randomTitleState = MutableStateFlow(RandomTitleState.None)
    val randomTitleState: StateFlow<RandomTitleState> = _randomTitleState.asStateFlow()
    var dominantColor = MutableStateFlow(Color.Unspecified)

    init {
        getAnimeIDs()
    }

    fun refresh() {
        getAnimeIDs()
    }

    fun updateDominantColor(newColor: Color) {
        val hsv = FloatArray(3)

        ColorUtils.colorToHSL(newColor.toArgb(), hsv)
        hsv[1] = (hsv[1] * 0.15f).coerceIn(0f, 1f)
        val adjustedColor = Color(ColorUtils.HSLToColor(hsv))

        dominantColor.value = adjustedColor
    }

    fun getRandomAnime() {
        screenModelScope.launch {
            val animeIDs = when (val state = _randomTitleState.value.animeIDs) {
                is DataState.Success -> state.data
                else -> null
            }
            when (
                val randTitle = repository.getRandomTitle(
                    ids = animeIDs,
                    count = _randomTitleState.value.animeCount
                )
            ) {
                is ReqRes.Error -> _randomTitleState.update {
                    it.copy(randomAnime = DataState.Error(randTitle.error))
                }

                is ReqRes.InProgress -> _randomTitleState.update {
                    it.copy(randomAnime = DataState.Loading)
                }

                is ReqRes.Success -> {
                    if (isLast()) _randomTitleState.update { it.copy(animeCount = 0) }
                    _randomTitleState.update {
                        it.copy(
                            randomAnime = DataState.Success(randTitle.data.toAnimeUI()),
                            animeCount = _randomTitleState.value.animeCount.plus(1),
                        )
                    }
                }
            }
        }
    }

    fun isLast(): Boolean {
        return _randomTitleState.value.maxTitles == _randomTitleState.value.animeCount
    }

    private fun getAnimeIDs() {
        val animeIDs = when (val state = _randomTitleState.value.animeIDs) {
            is DataState.Success -> state.data
            else -> null
        }
        if (animeIDs == null) {
            screenModelScope.launch {
                try {
                    withTimeout(10000L) {
                        when (val res = repository.getAnimeIDs()) {
                            is ReqRes.Error -> _randomTitleState
                                .update { it.copy(animeIDs = DataState.Error(res.error)) }

                            is ReqRes.InProgress -> _randomTitleState
                                .update { it.copy(animeIDs = DataState.Loading) }

                            is ReqRes.Success -> {
                                _randomTitleState.update {
                                    it.copy(
                                        maxTitles = res.data.size,
                                        animeIDs = DataState.Success(res.data),
                                        animeCount = 1,
                                    )
                                }
                                getRandomAnime()
                            }
                        }
                    }
                } catch (e: TimeoutCancellationException) {
                    _randomTitleState.update { it.copy(animeIDs = DataState.Error(e.cause)) }
                }
            }
        } else getRandomAnime()
    }
}