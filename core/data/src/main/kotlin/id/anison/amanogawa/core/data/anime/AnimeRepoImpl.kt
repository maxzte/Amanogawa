@file:OptIn(ExperimentalPagingApi::class)

package id.anison.amanogawa.core.data.anime

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import id.anison.amanogawa.core.data.config
import id.anison.amanogawa.core.data.runDataState
import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.domain.model.Anime
import id.anison.amanogawa.core.domain.model.AnimeDetails
import id.anison.amanogawa.core.domain.model.AnimeEpisodes
import id.anison.amanogawa.core.domain.repository.AnimeRepo
import id.anison.amanogawa.core.common.AppError
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.network.AmanogawaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import kotlin.collections.map

@Single
class AnimeRepoImpl(
    private val database: AmanogawaDatabase,
    private val api: AmanogawaApi,
) : AnimeRepo {
    private val animeDao = database.animeDao()
    private val currentAnimeDao = database.currentAnimeDao()

    override fun getAllWithPagination(): Flow<PagingData<Anime>> = Pager(
        config = config,
        remoteMediator = AnimeRemoteMediator(
            api = api,
            database = database,
            listType = AnimeListType.DEFAULT,
        ),
        pagingSourceFactory = {
            animeDao.pagingAll()
        },
    ).flow.map { pd -> pd.map { animeDbo -> animeDbo.toAnime() } }

    override suspend fun getCurrentAnimeList(): DataState<List<Anime>> = runDataState {
        api.getCurrentTitles().getOrThrow().map { dto -> dto.toAnime() }
    }

    override suspend fun getFirstPage(): DataState<List<Anime>> = runDataState {
        api.getTitlesByPage(1).getOrThrow().data.map { dto -> dto.toAnime() }
    }

    override suspend fun getAnimeListWithNewEpisodes(): DataState<List<Anime>> {
        return withContext(Dispatchers.IO) {
            when (val currentTitles = getCurrentAnimeList()) {
                is DataState.Success -> {
                    val local = currentAnimeDao.getAll()
                    if (local.isEmpty()) {
                        val list = currentTitles.data.map { it.toCurrentAnimeDbo() }
                        currentAnimeDao.insertCurrentAnimeList(animeList = list)

                        return@withContext DataState.Success(emptyList())
                    }

                    val localMap = local.associateBy { it.currentAnimeId }
                    val currentIds = currentTitles.data.map { it.animeId }

                    val newEpisodes = currentTitles.data.filter { remote ->
                        val localAnime = localMap[remote.animeId]
                        val isActuallyNew = localAnime?.newEpisode?.let { remote.newEpisode > it } ?: false
                        
                        // We notify if it's actually a new episode.
                        // If it's a delay (same episode number but in window), isActuallyNew will be false,
                        // so we remain silent.
                        isActuallyNew
                    }

                    database.withTransaction {
                        currentAnimeDao.insertCurrentAnimeList(
                            animeList = currentTitles.data.map { it.toCurrentAnimeDbo() }
                        )
                        currentAnimeDao.deleteNotCurrentAnimeByIds(currentIds)
                    }

                    DataState.Success(newEpisodes)
                }

                is DataState.Error -> {
                    DataState.Error(exception = currentTitles.exception)
                }

                else -> {
                    DataState.Error(exception = AppError.Unknown())
                }
            }
        }
    }

    override suspend fun getAnimeDetailsById(id: Long): DataState<AnimeDetails> {
        return runDataState {
            api.getAnime(id).getOrThrow().toAnimeDetails()
        }
    }

    override suspend fun getAnimeEpisodesById(id: Long): DataState<AnimeEpisodes> {
        return runDataState {
            api.getAnimeEpisodes(id).getOrThrow().toAnimeEpisodes()
        }
    }

    companion object {
        private const val TAG: String = "AnimeRepo"
    }

}
