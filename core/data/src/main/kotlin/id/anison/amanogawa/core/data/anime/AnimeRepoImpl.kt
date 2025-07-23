package id.anison.amanogawa.core.data.anime

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.data.settings.config
import id.anison.amanogawa.core.data.withDispatcherBlock
import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.database.anime.entity.CurrentAnimeDbo
import id.anison.amanogawa.core.network.AmanogawaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class AnimeRepoImpl(
    private val database: AmanogawaDatabase,
    private val api: AmanogawaApi,
    private val log: Logger,
) : AnimeRepo {
    private val animeDao = database.animeDao()
    private val currentAnimeDao = database.currentAnimeDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllWithPagination(): Flow<PagingData<Anime>> = Pager(
        config = config,
        remoteMediator = AnimeRemoteMediator(
            api = api,
            database = database,
            listType = AnimeListType.DEFAULT,
            log = log
        ),
        pagingSourceFactory = {
            animeDao.pagingAll()
        },
    ).flow.map { pd -> pd.map { animeDbo -> animeDbo.toAnime() } }

    override suspend fun getCurrentAnimeList(): DataState<List<Anime>> = withDispatcherBlock {
        api.getCurrentTitles().getOrThrow().map { dto -> dto.toAnime() }
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

                    val currentIds = currentTitles.data.map { it.animeId }.toSet()
                    val forDelete = local.filterNot { it.currentAnimeId in currentIds }

                    val newEpisodes = currentTitles.data.filter { remote ->
                        val localAnime = localMap[remote.animeId]
                        localAnime?.newEpisode?.let { remote.newEpisode > it } ?: false
                    }

                    currentAnimeDao.insertCurrentAnimeList(
                        animeList = currentTitles.data.map { it.toCurrentAnimeDbo() }
                    )

                    currentAnimeDao.deleteNotCurrentAnimeList(forDelete)

                    DataState.Success(newEpisodes)
                }

                else -> {
                    DataState.Error(exception = NoSuchElementException("Error getting current titles"))
                }
            }
        }
    }

    override suspend fun getAnimeDetailsById(id: Long): DataState<AnimeDetails> {
        return withDispatcherBlock {
            api.getAnime(id).getOrThrow().toAnimeDetails()
        }
    }

    override suspend fun getAnimeEpisodesById(id: Long): DataState<AnimeEpisodes> {
        return withDispatcherBlock {
            api.getAnimeEpisodes(id).getOrThrow().toAnimeEpisodes()
        }
    }

    companion object {
        private const val TAG: String = "Anime repo"
    }

}