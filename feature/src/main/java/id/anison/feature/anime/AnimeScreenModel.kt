package id.anison.feature.anime

import androidx.work.WorkManager
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.common.AndroidLogcatLogger
import id.anison.data.ReqRes
import id.anison.data.repository.AmanogawaRepository
import id.anison.feature.common.DataState
import id.anison.feature.model.AnimeUI
import id.anison.feature.model.toAnimeUI
import id.anison.feature.model.toDataFavorite
import id.anison.feature.model.toEpisodesUI
import id.anison.feature.model.toFavoriteUI
import id.anison.feature.work.NotificationWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnimeScreenModel(
    private val repository: AmanogawaRepository,
    private val workManager: WorkManager,
    private val log: AndroidLogcatLogger,
) : StateScreenModel<AnimeState>(AnimeState.None) {
    private val _animeState = MutableStateFlow(AnimeState.None)
    val animeState: StateFlow<AnimeState> = _animeState.asStateFlow()

    companion object {
        const val TAG = "AnimeScreenModel"
    }

    private fun getAnimeEpisodes(id: Long) {
        screenModelScope.launch {
            when (val res = repository.getAnimeEpisodes(id)) {
                is ReqRes.Error -> _animeState.update { state ->
                    state.copy(episodes = res.error?.cause?.let { DataState.Error(it) })
                }
                is ReqRes.InProgress -> _animeState.update { it.copy(episodes = DataState.Loading) }

                is ReqRes.Success -> _animeState.update { it.copy(
                        episodes = DataState.Success(res.data.toEpisodesUI().data),
                        isEpisodesLoaded = true
                    )
                }
            }
        }
    }

    private fun updateFavorite(id: Long) {
        screenModelScope.launch {
            when(val favorite = repository.getOneFavorite(id)) {
                is ReqRes.Success -> {
                    log.d(TAG, "UPD FAVORITE")
                    repository.addToFavorite(favorite.data.copy(isNewEpisode = false))
                }
                else -> log.d(TAG, "FAVORITE BY THIS ID:$id NOT EXIST")
            }
        }
    }

    fun refresh(id: Long) {
        getAnime(id)
    }

    fun getAnime(id: Long) {
        screenModelScope.launch {
            when (val res = repository.getAnime(id)) {
                is ReqRes.Error -> _animeState.update { state ->
                    state.copy(anime = res.error?.let { DataState.Error(it) })
                }
                is ReqRes.InProgress -> _animeState.update { it.copy(anime = DataState.Loading) }

                is ReqRes.Success -> {
                    updateFavorite(id)
                    _animeState.update { it.copy(
                            anime = DataState.Success(res.data.toAnimeUI()),
                            isLoaded = true
                        )
                    }
                    getAnimeEpisodes(id)
                }
            }
        }
    }

    fun isFavorite(id: Long): Flow<Boolean> {
        return repository.isFavorite(id)
    }

    fun addToFavorite(anime: AnimeUI) {
        screenModelScope.launch {
            repository.addToFavorite(anime.toFavoriteUI().toDataFavorite())
            if (anime.is_current) NotificationWorker.launch(workManager, anime.en_jp_name)
        }
    }

    fun removeFromFavorite(anime: AnimeUI) {
        screenModelScope.launch {
            repository.removeFromFavorite(anime.toFavoriteUI().toDataFavorite())
            if (anime.is_current) NotificationWorker.stopWork(workManager, anime.en_jp_name)
        }
    }
}