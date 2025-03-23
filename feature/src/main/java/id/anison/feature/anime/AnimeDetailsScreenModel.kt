package id.anison.feature.anime

import androidx.work.WorkManager
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.common.AndroidLogcatLogger
import id.anison.data.ReqRes
import id.anison.data.anime.AnimeRepo
import id.anison.data.favorite.FavoriteRepo
import id.anison.feature.anime.model.AnimeDetailsUI
import id.anison.feature.common.DataState
import id.anison.feature.anime.model.EpisodeUI
import id.anison.feature.anime.model.toAnimeDetailsDto
import id.anison.feature.anime.model.toAnimeUI
import id.anison.feature.anime.model.toEpisodesUI
import id.anison.feature.favorite.toFavoriteDto
import id.anison.feature.favorite.toFavoriteUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnimeDetailsScreenModel(
    private val animeRepo: AnimeRepo,
    private val favoriteRepo: FavoriteRepo,
    private val workManager: WorkManager,
    private val log: AndroidLogcatLogger,
) : StateScreenModel<AnimeState>(AnimeState()) {
    companion object {
        const val TAG = "AnimeDetailsScreenModel"
    }

    val animeState = MutableStateFlow(AnimeState())

    private fun loadEpisodes(id: Long) {
        screenModelScope.launch {
            when (val res = animeRepo.getAnimeEpisodes(id)) {
                is ReqRes.Success -> {
                    animeState.update { it.copy(episodes = DataState.Success(res.data.toEpisodesUI().data)) }
                }

                is ReqRes.Error -> {
                    animeState.update { it.copy(anime = DataState.Error(res.error)) }
                }

                is ReqRes.InProgress -> {
                    animeState.update { it.copy(anime = DataState.Loading) }
                }
            }
        }
    }

    private fun updateFavorite(id: Long) {
        screenModelScope.launch {
            when (val favorite = favoriteRepo.getFavoriteById(id)) {
                is ReqRes.Success -> {
                    log.d(TAG, "UPD FAVORITE")
                    favoriteRepo.addToFavorite(favorite.data.copy(isNewEpisode = false))
                }

                else -> {
                    log.d(TAG, "FAVORITE BY THIS ID:$id NOT EXIST")
                }
            }
        }
    }

    fun loadAnime(id: Long) {
        if (animeState.value.isAnimeLoaded) {
            log.d(TAG, "Anime already loaded, skipping API request")
            return
        }
        screenModelScope.launch {
            animeRepo.getAnime(id).collectLatest { res ->
                when (res) {
                    is ReqRes.Success -> {
                        animeState.update {
                            it.copy(
                                anime = DataState.Success(res.data.toAnimeUI()),
                                isAnimeLoaded = true,
                            )
                        }
                        //updateFavorite(id)
                        loadEpisodes(id)
                    }

                    is ReqRes.Error -> {
                        animeState.update { it.copy(anime = DataState.Error(res.error)) }
                    }

                    is ReqRes.InProgress -> {
                        animeState.update { it.copy(anime = DataState.Loading) }
                    }
                }
            }
        }
    }

    fun toggleFavorite(anime: AnimeDetailsUI) {
        screenModelScope.launch {
            if (animeState.value.isFavorite) {
                favoriteRepo.removeFromFavorite(anime.toFavoriteUI().toFavoriteDto())
                animeRepo.deleteAnimeDetails(anime.toAnimeDetailsDto())
                /*if (anime.is_current) {
                    NotificationWorker.stopWork(workManager, anime.en_jp_name)
                }*/
            } else {
                favoriteRepo.addToFavorite(anime.toFavoriteUI().toFavoriteDto())
                animeRepo.saveAnime(anime.toAnimeDetailsDto())
                /*if (anime.is_current) {
                    NotificationWorker.launch(workManager, anime.en_jp_name)
                }*/
            }
        }
    }

    fun isFavorite(id: Long): StateFlow<Boolean> {
        return favoriteRepo.isFavorite(id)
            .stateIn(
                scope = screenModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = false
            ).also { stateFlow ->
                screenModelScope.launch {
                    stateFlow.collectLatest { isFav ->
                        animeState.update { it.copy(isFavorite = isFav) }
                    }
                }
            }
    }

}

data class AnimeState(
    val anime: DataState<AnimeDetailsUI> = DataState.Loading,
    val episodes: DataState<List<EpisodeUI>> = DataState.Loading,
    val isFavorite: Boolean = false,
    val isAnimeLoaded: Boolean = false,
)