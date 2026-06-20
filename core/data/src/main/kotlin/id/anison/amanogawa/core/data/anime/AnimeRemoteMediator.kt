@file:OptIn(ExperimentalPagingApi::class)

package id.anison.amanogawa.core.data.anime

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.database.anime.entity.AnimeDbo
import id.anison.amanogawa.core.database.anime.entity.RemoteKeyDbo
import id.anison.amanogawa.core.domain.model.Filter
import id.anison.amanogawa.core.domain.model.TeamMemberType
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.network.AmanogawaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AnimeRemoteMediator(
    private val database: AmanogawaDatabase,
    private val api: AmanogawaApi,
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

        Log.d(tag = TAG) { "initialize: key = $listKey, remoteKey = $remoteKey, now = $now" }

        return if (listType == AnimeListType.FILTERED) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            if (remoteKey == null || (now - remoteKey.lastUpdated) >= cacheExpiryTimeMillis) {
                Log.d(tag = TAG) { "initialize → LAUNCH_INITIAL_REFRESH" }
                InitializeAction.LAUNCH_INITIAL_REFRESH
            } else {
                Log.d(tag = TAG) { "initialize → SKIP_INITIAL_REFRESH" }
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
                    Log.d(tag = TAG) { "LoadType.PREPEND received, ending pagination." }
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getKey(listKey)
                    remoteKey?.nextPage ?: return MediatorResult.Success(true)
                }
            }
            Log.d(tag = TAG) { "Loading page: $page for load type: $loadType" }

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

            Log.d(tag = TAG) { "filter is $filter" }
            Log.d(tag = TAG) { "response is ${response.getOrNull()}" }

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
                Log.d(tag = TAG) { "Inserted ${titles.data.size} items for page $page." }
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Log.e(tag = TAG) { "Error during data loading: ${e.message}" }
            MediatorResult.Error(e)
        }
    }

    private fun Filter.generateCacheKey(): String {
        val genreIds = genres.map { it.genreId }.sorted().joinToString("_")
        val voiceIds =
            teamMembers.filter { it.teamMemberType == TeamMemberType.VOICE }.map { it.teamMemberId }
                .sorted().joinToString("_")
        val soundIds =
            teamMembers.filter { it.teamMemberType == TeamMemberType.SOUND }.map { it.teamMemberId }
                .sorted().joinToString("_")
        val transIds = teamMembers.filter { it.teamMemberType == TeamMemberType.TRANSLATOR }
            .map { it.teamMemberId }.sorted().joinToString("_")
        val yearIds = years.sorted().joinToString("_")

        return "g=$genreIds&y=$yearIds&v=$voiceIds&s=$soundIds&t=$transIds"
    }

    companion object {
        const val TAG = "AnimeRemoteMediator"
    }

}

enum class AnimeListType {
    DEFAULT,
    FILTERED,
}
