package id.anison.amanogawa.core.data.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import id.anison.amanogawa.core.common.AndroidLogcatLogger
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.data.anime.Anime
import id.anison.amanogawa.core.data.anime.AnimeDetails
import id.anison.amanogawa.core.data.anime.AnimeListType
import id.anison.amanogawa.core.data.anime.AnimeRemoteMediator
import id.anison.amanogawa.core.data.anime.AnimeRepo
import id.anison.amanogawa.core.data.anime.toAnime
import id.anison.amanogawa.core.data.filter.Filter
import id.anison.amanogawa.core.data.filter.FilterType
import id.anison.amanogawa.core.data.filter.TeamMember
import id.anison.amanogawa.core.data.filter.TeamMemberType
import id.anison.amanogawa.core.data.filter.toGenre
import id.anison.amanogawa.core.data.settings.config
import id.anison.amanogawa.core.data.withDispatcherBlock
import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.database.anime.entity.AnimeIdsDbo
import id.anison.amanogawa.core.database.filter.FilterTypeDbo
import id.anison.amanogawa.core.network.AmanogawaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class SearchRepoImpl(
    private val api: AmanogawaApi,
    private val database: AmanogawaDatabase,
    private val animeRepo: AnimeRepo,
    private val log: Logger,
) : SearchRepo {
    private val searchResultDao = database.searchResultDao()
    private val animeIdsDao = database.animeIdsDao()

    override fun searchTitle(query: String): Flow<DataState<List<SearchResult>>> = flow {
        try {
            val response = api.searchAnimeByName(query.trim())
            response.fold(
                onSuccess = { animeList ->
                    emit(DataState.Success(animeList.map { it.toSearchResult() }))
                },
                onFailure = { e -> emit(DataState.Error(e)) }
            )
        } catch (e: Exception) {
            log.d(TAG, "EXCEPTION while searchTitle $e")
            emit(DataState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addToSearchHistory(anime: SearchResult) {
        searchResultDao.insertFavorite(anime.toSearchResultDbo())
    }

    override fun getSearchHistory(): Flow<DataState<List<SearchResult>>> = flow {
        emit(DataState.Loading)
        emitAll(
            searchResultDao.getAll().map { list ->
                DataState.Success(list.map { it.toSearchResult() })
            }
        )
    }.catch { e ->
        log.d(TAG, "Error getting data from database. Cause = $e")
        emit(DataState.Error(e))
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteItemFromSearchHistory(dataAnime: SearchResult) {
        return withContext(Dispatchers.IO) {
            searchResultDao.deleteSearchResult(dataAnime.toSearchResultDbo())
        }
    }

    override suspend fun clearSearchHistory() {
        return withContext(Dispatchers.IO) {
            searchResultDao.clearSearchHistory()
        }
    }

    override suspend fun getTitlesBySearchFilter(): Flow<PagingData<Anime>> {
        val genres = database.filterDao().getFilterWithGenres(FilterTypeDbo.SEARCH).genres
        val dubbers = database.filterDao().getFilterWithDubbers(FilterTypeDbo.SEARCH).dubbers
        val translators =
            database.filterDao().getFilterWithTranslators(FilterTypeDbo.SEARCH).translators
        val soundEngineers =
            database.filterDao().getFilterWithSoundEngineers(FilterTypeDbo.SEARCH).soundEngineers
        val years = database.filterDao().getFilterWithYears(FilterTypeDbo.SEARCH).years

        val resultList = buildList {
            dubbers.forEach { dubber ->
                add(
                    TeamMember(
                        teamMemberId = dubber.dubberId,
                        teamMemberType = TeamMemberType.VOICE
                    )
                )
            }
            translators.forEach { translator ->
                add(
                    TeamMember(
                        teamMemberId = translator.translatorId,
                        teamMemberType = TeamMemberType.TRANSLATOR
                    )
                )
            }
            soundEngineers.forEach { engineer ->
                add(
                    TeamMember(
                        teamMemberId = engineer.soundEngineerId,
                        teamMemberType = TeamMemberType.SOUND
                    )
                )
            }
        }

        val filter = Filter(
            teamMembers = resultList,
            genres = genres.map { it.toGenre() },
            years = years.map { it.year },
        )

        return getTitlesByFilter(filter = filter)
    }

    override suspend fun getTitlesByFilterName(
        filterParamId: Long?,
        filterName: String,
        filterType: FilterType
    ): Flow<PagingData<Anime>> {
        val filter = when (filterType) {
            FilterType.GENRE -> {
                val genre = database.genreDao().getGenreByGenreName(genreName = filterName)
                Filter(genres = listOf(genre.toGenre()))
            }

            FilterType.DUBBER -> {
                val dubber =
                    database.teamMemberDao().getTeamMemberWithRoles(name = filterName)?.dubbers
                log.d(TAG, "tm is $dubber")
                val teamMember = TeamMember(
                    teamMemberId = dubber?.first()?.dubberId ?: filterParamId!!,
                    teamMemberType = TeamMemberType.VOICE
                )
                Filter(teamMembers = listOf(teamMember))
            }

            FilterType.TRANSLATOR -> {
                val translator =
                    database.teamMemberDao().getTeamMemberWithRoles(name = filterName)?.translators
                log.d(TAG, "tm is $translator")
                val teamMember = TeamMember(
                    teamMemberId = translator?.first()?.translatorId ?: filterParamId!!,
                    teamMemberType = TeamMemberType.TRANSLATOR
                )
                Filter(teamMembers = listOf(teamMember))
            }

            FilterType.SOUND_ENGINEER -> {
                val soundEngineer =
                    database.teamMemberDao()
                        .getTeamMemberWithRoles(name = filterName)?.soundEngineers
                log.d(TAG, "tm is $soundEngineer")
                val teamMember = TeamMember(
                    teamMemberId = soundEngineer?.first()?.soundEngineerId ?: filterParamId!!,
                    teamMemberType = TeamMemberType.SOUND
                )
                Filter(teamMembers = listOf(teamMember))
            }

            FilterType.ALL_TEAM_MEMBER -> {
                val teamMembers: MutableList<TeamMember> = mutableListOf()
                val res = database.teamMemberDao().getTeamMemberWithRoles(name = filterName)
                if (!res?.dubbers.isNullOrEmpty()) {
                    teamMembers.add(
                        TeamMember(
                            teamMemberId = res.dubbers.first().dubberId,
                            teamMemberType = TeamMemberType.VOICE
                        )
                    )
                }
                if (!res?.translators.isNullOrEmpty()) {
                    teamMembers.add(
                        TeamMember(
                            teamMemberId = res.translators.first().translatorId,
                            teamMemberType = TeamMemberType.TRANSLATOR
                        )
                    )
                }
                if (!res?.soundEngineers.isNullOrEmpty()) {
                    teamMembers.add(
                        TeamMember(
                            teamMemberId = res.soundEngineers.first().soundEngineerId,
                            teamMemberType = TeamMemberType.SOUND
                        )
                    )
                }

                Filter(teamMembers = teamMembers)
            }

            else -> Filter(years = listOf(filterName.toShort()))
        }
        return getTitlesByFilter(filter = filter)
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getTitlesByFilter(filter: Filter): Flow<PagingData<Anime>> = Pager(
        config = config,
        remoteMediator = AnimeRemoteMediator(
            api = api,
            database = database,
            log = log,
            listType = AnimeListType.FILTERED,
            filter = filter
        ),
        pagingSourceFactory = { ExtendedSearchPagingSource(api, filter) },
    ).flow.map { it -> it.map { it.toAnime() } }

    override fun getRandomTitles(): Flow<PagingData<AnimeDetails>> = Pager(
        config = config,
    ) {
        RandomAnimePagingSource(
            api = api,
            log = log,
        )
    }.flow

    fun getRandomAnime(
        ids: List<Long>,
        count: Int = 0
    ): Flow<DataState<AnimeDetails>> = flow {
        log.d(TAG, "count is: $count")
        emit(DataState.Loading)

        if (ids.isEmpty()) {
            emit(DataState.Error(Exception("IDs list is empty")))
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

        emit(animeRepo.getAnimeDetailsById(randomId))
    }.catch { e ->
        log.d(TAG, "EXCEPTION while getRandomAnime: $e")
        emit(DataState.Error(e))
    }.flowOn(Dispatchers.IO)

    private var totalPages: Int = -1
    private val usedPages = mutableSetOf<Int>()

    suspend fun getTotalPages(): Int {
        if (totalPages != -1) return totalPages
        val response = api.getTitlesByPage(1).getOrThrow()
        totalPages = response.pages
        return totalPages
    }

//    suspend fun getRandomPage(): List<AnimeDetails> {
//        val total = getTotalPages()
//        val availablePages = (1..total).filterNot { usedPages.contains(it) }
//        val randomPage = availablePages.randomOrNull()
//            ?: return emptyList()
//
//        return try {
//            val response = api.getTitlesByPage(randomPage).getOrThrow()
//            usedPages += randomPage
//            response.data.map { it.toAnime().toAnimeUI() }.shuffled()
//        } catch (e: Exception) {
//            log.e("SearchRepo", "Error loading page $randomPage: ${e.message}")
//            emptyList()
//        }
//    }

    fun clearUsedPages() {
        usedPages.clear()
    }

//    suspend fun getTeam(): DataState<List<TeamMember>> = withDispatcherBlock {
//        api.getTeam().getOrThrow().map { it.toTeamMember() }
//    }

    companion object Companion {
        private const val TAG: String = "SearchRepo"
    }

}