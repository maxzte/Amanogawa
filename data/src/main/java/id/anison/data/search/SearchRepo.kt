package id.anison.data.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import id.anison.api.AmanogawaApi
import id.anison.common.AndroidLogcatLogger
import id.anison.data.ReqRes
import id.anison.data.anime.AnimeDto
import id.anison.data.anime.AnimeRepo
import id.anison.data.anime.toAnimeDto
import id.anison.data.anime.AnimeRemoteMediator
import id.anison.data.TeamMemberDto
import id.anison.data.anime.AnimeDetailsDto
import id.anison.data.settings.config
import id.anison.data.toTeamMemberDto
import id.anison.database.AmanogawaDatabase
import id.anison.database.anime.entity.AnimeIdsDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class SearchRepo(
    private val database: AmanogawaDatabase,
    private val api: AmanogawaApi,
    private val log: AndroidLogcatLogger,
    private val animeRepo: AnimeRepo,
) {
    private val foundAnimeDao = database.foundAnimeDao()
    private val filterDao = database.filterDao()
    private val animeIdsDao = database.animeIdsDao()

    companion object {
        const val TAG: String = "Search repo"
    }

    suspend fun addToSearchHistory(anime: FoundAnimeDto) {
        foundAnimeDao.insertFavorite(anime.toFoundAnimeDbo())
    }

    fun getSearchHistory(): Flow<ReqRes<List<FoundAnimeDto>>> = flow {
        emit(ReqRes.InProgress())
        try {
            val dbRequest = foundAnimeDao.getAll()
            emitAll(
                dbRequest.map { list ->
                    ReqRes.Success(list.map { it.toFoundAnimeDto() })
                }
            )
        } catch (e: Exception) {
            log.e(TAG, "Error getting data from database. Cause = $e")
            emit(ReqRes.Error(error = e))
        }
    }

    suspend fun deleteItemFromSearchHistory(dataAnime: FoundAnimeDto) {
        foundAnimeDao.deleteOneFoundAnime(dataAnime.toFoundAnimeDbo())
    }

    suspend fun clearSearchHistory() {
        foundAnimeDao.clearSearchHistory()
    }

    fun getRandomAnime(
        ids: List<Long>,
        count: Int = 0
    ): Flow<ReqRes<AnimeDetailsDto>> = flow {
        log.d(TAG, "count is: $count")

        emit(ReqRes.InProgress())

        if (ids.isEmpty()) {
            emit(ReqRes.Error(error = Exception("IDs list is empty")))
            return@flow
        }

        val existingIds = animeIdsDao.getAll().map { it.id }.toSet()
        var unusedIds = ids.filterNot { it in existingIds }

        if (unusedIds.isEmpty()) {
            animeIdsDao.deleteAllIDs()
            unusedIds = ids
        }

        val randomId = unusedIds.random()
        animeIdsDao.insertAnimeIds(AnimeIdsDbo(id = randomId))

        emitAll(animeRepo.getAnime(randomId))
    }.catch { e ->
        log.d(TAG, "EXCEPTION while getRandomAnime: $e")
        emit(ReqRes.Error(error = e))
    }.flowOn(Dispatchers.IO)


    suspend fun getAnimeIDs(): ReqRes<List<Long>> = withContext(Dispatchers.IO) {
        try {
            val animeIDsResult = api.getCountAnimeAndTheirIDs()
            animeIDsResult.fold(
                onSuccess = { animeIDs -> ReqRes.Success(animeIDs.id) },
                onFailure = { e ->
                    log.d(TAG, "Failed to fetch IDs: $e")
                    ReqRes.Error(null, e.cause)
                }
            )
        } catch (e: Exception) {
            log.d(TAG, "EXCEPTION while getAnimeIDs $e")
            ReqRes.Error(null, e)
        }
    }

    suspend fun getExistingAnimeCount(): Int = withContext(Dispatchers.IO) {
        return@withContext animeIdsDao.getAll().map { it.id }.size
    }

    suspend fun getFilters(): ReqRes<FilterDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFilter()
            response.fold(
                onSuccess = { filter ->
                    log.d(TAG, "Success: $filter")
                    val filterDbo = filter.toFilterDbo()
                    filterDao.insert(filterDbo)
                    ReqRes.Success(filter.toFilterDto())
                },
                onFailure = { e ->
                    log.d(TAG, "API error: $e")
                    val cachedData = filterDao.getAll().toFilterDto()
                    ReqRes.Error(cachedData, e)
                }
            )
        } catch (e: Exception) {
            log.d(TAG, "EXCEPTION while getFilters $e")
            ReqRes.Error(filterDao.getAll().toFilterDto(), e)
        }
    }

    fun searchTitle(query: String): Flow<ReqRes<List<FoundAnimeDto>>> = flow {
        try {
            val response = api.searchAnimeByName(query.trim())
            response.fold(
                onSuccess = { animeList ->
                    emit(ReqRes.Success(animeList.map { it.toFoundAnimeDto() }))
                },
                onFailure = { e -> emit(ReqRes.Error(null, e)) }
            )
        } catch (e: Exception) {
            log.d(TAG, "EXCEPTION while searchTitle $e")
            emit(ReqRes.Error(null, e))
        }
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalPagingApi::class)
    fun getFilteredTitles(filter: FilterDto): Flow<PagingData<AnimeDto>> = Pager(
        config = config,
        remoteMediator = AnimeRemoteMediator(api, database, log),
        pagingSourceFactory = { ExtendedSearchPagingSource(api, filter) },
    ).flow.map { it -> it.map { it.toAnimeDto() } }

    suspend fun getTeam(): ReqRes<List<TeamMemberDto>> = withContext(Dispatchers.IO) {
        try {
            api.getTeam().fold(
                onSuccess = { res ->
                    log.d(TAG, "getTeam is success: $res")
                    ReqRes.Success(res.map { it.toTeamMemberDto() })
                },
                onFailure = { e ->
                    log.d(TAG, "getTeam is error: $e")
                    ReqRes.Error(null, e.cause)
                }
            )
        } catch (e: Exception) {
            log.d(TAG, "getTeam is error: $e")
            ReqRes.Error(null, e)
        }
    }
}