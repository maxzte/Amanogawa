package id.anison.amanogawa.core.data.anime

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.data.filter.Filter
import id.anison.amanogawa.core.data.filter.TeamMemberType
import id.anison.amanogawa.core.data.filter.generateCacheKey
import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.database.anime.entity.AnimeDbo
import id.anison.amanogawa.core.database.anime.entity.RemoteKeyDbo
import id.anison.amanogawa.core.network.AmanogawaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class AnimeRemoteMediator(
    private val database: AmanogawaDatabase,
    private val api: AmanogawaApi,
    private val log: Logger,
    private val listType: AnimeListType,
    private val filter: Filter? = null,
) : RemoteMediator<Int, AnimeDbo>() {

    private val remoteKeyDao = database.remoteKeyDao()
    private val animeDao = database.animeDao()
    private val listKey = if (listType == AnimeListType.FILTERED) {
        "FILTERED_" + filter?.generateCacheKey()
    } else {
        listType.name
    }

    override suspend fun initialize(): InitializeAction {
        val remoteKey = remoteKeyDao.getKey(listKey)
        val now = System.currentTimeMillis()
        val cacheExpiryTimeMillis = TimeUnit.HOURS.toMillis(1)

        log.d(TAG, "initialize: key = $listKey, remoteKey = $remoteKey, now = $now")

        return if (listType == AnimeListType.FILTERED) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            if (remoteKey == null || (now - remoteKey.lastUpdated) >= cacheExpiryTimeMillis) {
                log.d(TAG, "initialize → LAUNCH_INITIAL_REFRESH")
                InitializeAction.LAUNCH_INITIAL_REFRESH
            } else {
                log.d(TAG, "initialize → SKIP_INITIAL_REFRESH")
                InitializeAction.SKIP_INITIAL_REFRESH
            }
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AnimeDbo>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    1
                }

                LoadType.PREPEND -> {
                    log.d(TAG, "LoadType.PREPEND received, ending pagination.")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getKey(listKey)
                    remoteKey?.nextPage ?: return MediatorResult.Success(true)
                }
            }
            log.d(TAG, "Loading page: $page for load type: $loadType")

            val response = withContext(Dispatchers.IO) {
                when (listType) {
                    AnimeListType.DEFAULT -> api.getTitlesByPage(page)

                    AnimeListType.FILTERED -> {
                        val voices = filter!!.teamMembers
                            .filter { it.teamMemberType == TeamMemberType.VOICE }
                            .map { it.teamMemberId }
                            .takeIf { it.isNotEmpty() }

                        val sounds = filter.teamMembers
                            .filter { it.teamMemberType == TeamMemberType.SOUND }
                            .map { it.teamMemberId }
                            .takeIf { it.isNotEmpty() }

                        val translators = filter.teamMembers
                            .filter { it.teamMemberType == TeamMemberType.TRANSLATOR }
                            .map { it.teamMemberId }
                            .takeIf { it.isNotEmpty() }

                        val genres = filter.genres
                            .map { it.genreId }
                            .takeIf { it.isNotEmpty() }

                        val years = filter.years.takeIf { it.isNotEmpty() }

                        api.getFilteredTitles(
                            page = page,
                            years = years,
                            genres = genres,
                            voices = voices,
                            sounds = sounds,
                            translators = translators
                        )
                    }
                }
            }

            log.d(TAG, "filter is $filter")
            log.d(TAG, "response is ${response.getOrNull()}")

            val titles = response.getOrThrow()
            val endOfPaginationReached = titles.data.isEmpty() || titles.currentPage >= titles.pages

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    animeDao.clearAllByListKey(listKey)
                    remoteKeyDao.deleteKey(listKey)
                }

                if (titles.data.isNotEmpty()) {
                    remoteKeyDao.insertKey(
                        key = RemoteKeyDbo(
                            key = listKey,
                            currentPage = titles.currentPage,
                            nextPage = if (titles.currentPage < titles.pages) titles.currentPage + 1 else null,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                }

                animeDao.insert(titles.data.map { it.toAnime().toAnimeDbo(listKey) })
                log.d(TAG, "Inserted ${titles.data.size} items for page $page.")
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            log.e(TAG, "Error during data loading: ${e.message}")
            MediatorResult.Error(e)
        }
    }

    companion object {
        const val TAG = "AnimeRemoteMediator"
    }

}

enum class AnimeListType {
    DEFAULT,
    FILTERED,
}
