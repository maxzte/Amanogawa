package id.anison.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import id.anison.api.AmanogawaApi
import id.anison.common.AndroidLogcatLogger
import id.anison.data.ReqRes
import id.anison.data.local.CurrentTitlesPagingSource
import id.anison.data.local.ExtendedSearchPagingSource
import id.anison.data.local.TitlesPagingSource
import id.anison.data.model.DataAnime
import id.anison.data.model.DataCurrentTitle
import id.anison.data.model.DataEpisode
import id.anison.data.model.DataEpisodes
import id.anison.data.model.DataFavorite
import id.anison.data.model.DataFilter
import id.anison.data.model.DataFoundAnime
import id.anison.data.model.DataTeamItem
import id.anison.data.model.DataTitle
import id.anison.data.remote.TitlesRemoteMediator
import id.anison.data.toCurrentDataTitle
import id.anison.data.toDataAnime
import id.anison.data.toDataEpisodes
import id.anison.data.toDataFavorite
import id.anison.data.toDataFilter
import id.anison.data.toDataFoundAnime
import id.anison.data.toDataTeamItem
import id.anison.data.toDataTitle
import id.anison.data.toFavoriteDbo
import id.anison.data.toFilterDbo
import id.anison.data.toFoundAnimeDbo
import id.anison.database.AmanogawaDatabase
import id.anison.database.model.AnimeIDsDbo
import id.anison.database.model.FavoriteDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AmanogawaRepository(
    private val database: AmanogawaDatabase,
    private val api: AmanogawaApi,
    private val log: AndroidLogcatLogger,
) {
    companion object {
        const val TAG: String = "Amanogawa repository"
        val config: PagingConfig = PagingConfig(
            pageSize = 10,
            prefetchDistance = 10 / 2,
            enablePlaceholders = false,
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAll(): Flow<PagingData<DataTitle>> = Pager(
        config = config,
        remoteMediator = TitlesRemoteMediator(api, database, log),
        pagingSourceFactory = { TitlesPagingSource(api, log) },
    ).flow.map { it -> it.map { it.toDataTitle() } }

    fun getAllCurrent(): Flow<PagingData<DataCurrentTitle>> = Pager(config = config) {
        CurrentTitlesPagingSource(api, log)
    }.flow.map { it -> it.map { it.toCurrentDataTitle() } }

    suspend fun getAnime(id: Long): ReqRes<DataAnime> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAnime(id)
            response.fold(
                onSuccess = { anime ->
                    ReqRes.Success(anime.toDataAnime())
                },
                onFailure = { e ->
                    ReqRes.Error(null, e)
                },
            )
        } catch (e: Exception) {
            log.d(TAG, "EXCEPTION while getAnime $e")
            ReqRes.Error(null, e)
        }
    }

    suspend fun getAnimeEpisodes(id: Long): ReqRes<DataEpisodes<DataEpisode>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getAnimeEpisodes(id)
                response.fold(
                    onSuccess = { anime ->
                        ReqRes.Success(anime.toDataEpisodes())
                    },
                    onFailure = { e ->
                        ReqRes.Error(response.getOrThrow().toDataEpisodes(), e)
                    }
                )
            } catch (e: Exception) {
                log.d(TAG, "EXCEPTION while getAnimeEpisodes $e")
                ReqRes.Error(null, e)
            }
        }

    fun getFavorites(): Flow<ReqRes<List<DataFavorite>>> = flow {
        emit(ReqRes.InProgress())
        val dbRequest = database.favoriteDao.getAll()
        emitAll(
            dbRequest.map { list ->
                ReqRes.Success(list.map { it.toDataFavorite() })
            }
        )
    }.catch { e ->
        log.e(TAG, "Error getting data from database. Cause = $e")
        emit(ReqRes.Error(error = e))
    }

    fun isFavorite(id: Long): Flow<Boolean> {
        return database.favoriteDao.isFavorite(id)
    }

    suspend fun addToFavorite(favorite: DataFavorite) {
        log.d(TAG, "Adding favorite to DB. Fav is: $favorite")
        database.favoriteDao.insertFavorite(favorite.toFavoriteDbo())
    }

    suspend fun getOneFavorite(id: Long): ReqRes<DataFavorite> {
        return try {
            val dbRequest = database.favoriteDao.getOne(id)
            log.d(TAG, "Getting one favorite from DB. Fav is: $dbRequest")
            if (dbRequest != null) ReqRes.Success(dbRequest.toDataFavorite())
            else ReqRes.Error(null, null)
        } catch (e: Exception) {
            log.e(TAG, "Error getting data from database. Cause = $e")
            ReqRes.Error(null, e)
        }
    }

    suspend fun removeFromFavorite(favorite: DataFavorite) {
        database.favoriteDao.deleteOneFavorite(favorite.toFavoriteDbo())
    }

    suspend fun clearFavoriteList() {
        database.favoriteDao.deleteAllFavorites()
    }

    suspend fun addToSearchHistory(anime: DataFoundAnime) {
        database.foundAnimeDao.insertFavorite(anime.toFoundAnimeDbo())
    }

    fun getSearchHistory(): Flow<ReqRes<List<DataFoundAnime>>> = flow {
        emit(ReqRes.InProgress())
        try {
            val dbRequest = database.foundAnimeDao.getAll()
            emitAll(
                dbRequest.map { list ->
                    ReqRes.Success(list.map { it.toDataFoundAnime() })
                }
            )
        } catch (e: Exception) {
            log.e(TAG, "Error getting data from database. Cause = $e")
            emit(ReqRes.Error(error = e))
        }
    }

    suspend fun deleteItemFromSearchHistory(dataAnime: DataFoundAnime) {
        database.foundAnimeDao.deleteOneFoundAnime(dataAnime.toFoundAnimeDbo())
    }

    suspend fun clearSearchHistory() {
        database.foundAnimeDao.clearSearchHistory()
    }

    suspend fun getRandomTitle(ids: List<Long>?, count: Int = 0): ReqRes<DataAnime> {
        log.d(TAG, "count is: $count")
        return withContext(Dispatchers.IO) {
            try {
                if (ids == null) return@withContext ReqRes.Error(null, Exception("IDs is null"))
                var existingIds = database.animeIDsDao.getAll().map { it.id }
                var unusedIds = ids.filterNot { it in existingIds }
                if (unusedIds.isEmpty()) {
                    database.animeIDsDao.deleteAllIDs()
                    existingIds = emptyList()
                    unusedIds = ids
                    log.d(TAG, "after deleting")
                }
                val randomId = unusedIds.random()
                database.animeIDsDao.insertID(AnimeIDsDbo(id = randomId))
                getAnime(randomId)
            } catch (e: Exception) {
                log.d(TAG, "EXCEPTION while getRandomTitle $e")
                ReqRes.Error(null, e.cause)
            }
        }
    }

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
        return@withContext database.animeIDsDao.getAll().map { it.id }.size
    }

    suspend fun getFilters(): ReqRes<DataFilter> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFilter()
            response.fold(
                onSuccess = { filter ->
                    log.d(TAG, "Success: $filter")
                    val filterDbo = filter.toFilterDbo()
                    database.filterDao.insert(filterDbo)
                    ReqRes.Success(filter.toDataFilter())
                },
                onFailure = { e ->
                    log.d(TAG, "API error: $e")
                    val cachedData = database.filterDao.getAll().toDataFilter()
                    ReqRes.Error(cachedData, e)
                }
            )
        } catch (e: Exception) {
            log.d(TAG, "EXCEPTION while getFilters $e")
            ReqRes.Error(database.filterDao.getAll().toDataFilter(), e)
        }
    }

    fun searchTitle(query: String): Flow<ReqRes<List<DataFoundAnime>>> = flow {
        try {
            val response = api.searchAnimeByName(query.trim())
            response.fold(
                onSuccess = { animeList ->
                    emit(ReqRes.Success(animeList.map { it.toDataFoundAnime() }))
                },
                onFailure = { e -> emit(ReqRes.Error(null, e)) }
            )
        } catch (e: Exception) {
            log.d(TAG, "EXCEPTION while searchTitle $e")
            emit(ReqRes.Error(null, e))
        }
    }.flowOn(Dispatchers.IO)

    @OptIn(ExperimentalPagingApi::class)
    fun getFilteredTitles(filter: DataFilter): Flow<PagingData<DataTitle>> = Pager(
        config = config,
        remoteMediator = TitlesRemoteMediator(api, database, log),
        pagingSourceFactory = { ExtendedSearchPagingSource(api, filter) },
    ).flow.map { it -> it.map { it.toDataTitle() } }

    suspend fun getTeam(): ReqRes<List<DataTeamItem>> = withContext(Dispatchers.IO) {
        try {
            api.getTeam().fold(
                onSuccess = { res ->
                    log.d(TAG, "getTeam is success: $res")
                    ReqRes.Success(res.map { it.toDataTeamItem() })
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

    suspend fun checkForNewEpisode(favoriteFromDB: DataFavorite): Boolean {
        val newEpisode = getAnimeEpisodes(favoriteFromDB.id).data?.data?.lastOrNull()?.number
        if (newEpisode == null) {
            log.e(TAG, "Error getting episodes for ID: ${favoriteFromDB.id}")
            return false
        }
        return if (favoriteFromDB.episodes == newEpisode) {
            false
        } else {
            log.d(
                TAG,
                "from DB is ${favoriteFromDB.episodes}\nfrom fetch is $newEpisode"
            )
            database.favoriteDao.insertFavorite(
                FavoriteDbo(
                    isFavorite = favoriteFromDB.isFavorite,
                    isCurrent = favoriteFromDB.isCurrent,
                    id = favoriteFromDB.id,
                    name = favoriteFromDB.name,
                    en_jap_name = favoriteFromDB.en_jap_name,
                    season = favoriteFromDB.season,
                    year = favoriteFromDB.year,
                    poster = favoriteFromDB.poster,
                    views = favoriteFromDB.views,
                    liked = favoriteFromDB.liked,
                    disliked = favoriteFromDB.disliked,
                    episodes = newEpisode,
                    isNewEpisode = true,
                )
            )
            true
        }
    }
}