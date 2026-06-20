package id.anison.amanogawa.core.data.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.data.anime.AnimeListType
import id.anison.amanogawa.core.data.anime.AnimeRemoteMediator
import id.anison.amanogawa.core.data.anime.toAnime
import id.anison.amanogawa.core.data.config
import id.anison.amanogawa.core.data.filter.toGenre
import id.anison.amanogawa.core.data.toAppError
import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.database.filter.FilterTypeDbo
import id.anison.amanogawa.core.database.teammember.DubberDbo
import id.anison.amanogawa.core.database.teammember.SoundEngineerDbo
import id.anison.amanogawa.core.database.teammember.TeamMemberWithRoles
import id.anison.amanogawa.core.database.teammember.TranslatorDbo
import id.anison.amanogawa.core.domain.model.Anime
import id.anison.amanogawa.core.domain.model.AnimeDetails
import id.anison.amanogawa.core.domain.model.Filter
import id.anison.amanogawa.core.domain.model.FilterType
import id.anison.amanogawa.core.domain.model.SearchResult
import id.anison.amanogawa.core.domain.model.TeamMember
import id.anison.amanogawa.core.domain.model.TeamMemberType
import id.anison.amanogawa.core.domain.repository.SearchRepo
import id.anison.amanogawa.core.model.DataState
import id.anison.amanogawa.core.network.AmanogawaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class SearchRepoImpl(
    private val api: AmanogawaApi,
    private val database: AmanogawaDatabase,
) : SearchRepo {
    private val searchResultDao = database.searchResultDao()

    override fun searchTitle(query: String): Flow<DataState<List<SearchResult>>> = flow {
        val response = api.searchAnimeByName(query.trim())
        response.fold(
            onSuccess = { animeList ->
                emit(DataState.Success(animeList.map { it.toSearchResult() }))
            },
            onFailure = { e ->
                Log.d(tag = TAG) { "Error while searchTitle $e" }
                emit(DataState.Error(exception = e.toAppError()))
            }
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun addToSearchHistory(anime: SearchResult) {
        searchResultDao.insertSearchResult(anime.toSearchResultDbo())
    }

    override fun getSearchHistory(): Flow<DataState<List<SearchResult>>> = flow {
        emit(DataState.Loading)
        emitAll(
            searchResultDao.getAll().map { list ->
                DataState.Success(list.map { it.toSearchResult() })
            }
        )
    }.catch { e ->
        Log.d(tag = TAG) { "Error getting data from database. Cause = $e" }
        emit(DataState.Error(exception = e.toAppError()))
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

        val filter = Filter(
            teamMembers = buildList {
                addAll(dubbers.toVoiceTeamMembers())
                addAll(translators.toTranslatorTeamMembers())
                addAll(soundEngineers.toSoundTeamMembers())
            },
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
                Filter(genres = listOfNotNull(genre?.toGenre()))
            }

            FilterType.DUBBER -> Filter(teamMembers = listOf(voiceMember(filterName, filterParamId)))
            FilterType.TRANSLATOR -> Filter(teamMembers = listOf(translatorMember(filterName, filterParamId)))
            FilterType.SOUND_ENGINEER -> Filter(teamMembers = listOf(soundMember(filterName, filterParamId)))

            FilterType.ALL_TEAM_MEMBER -> {
                Filter(teamMembers = allTeamMembers(filterName))
            }

            else -> Filter(years = listOf(filterName.toShort()))
        }
        return getTitlesByFilter(filter = filter)
    }

    private suspend fun voiceMember(filterName: String, filterParamId: Long?) = teamMember(
        filterName = filterName,
        filterParamId = filterParamId,
        type = TeamMemberType.VOICE,
        selector = { dubbers.firstOrNull()?.dubberId },
    )

    private suspend fun translatorMember(filterName: String, filterParamId: Long?) = teamMember(
        filterName = filterName,
        filterParamId = filterParamId,
        type = TeamMemberType.TRANSLATOR,
        selector = { translators.firstOrNull()?.translatorId },
    )

    private suspend fun soundMember(filterName: String, filterParamId: Long?) = teamMember(
        filterName = filterName,
        filterParamId = filterParamId,
        type = TeamMemberType.SOUND,
        selector = { soundEngineers.firstOrNull()?.soundEngineerId },
    )

    private suspend fun teamMember(
        filterName: String,
        filterParamId: Long?,
        type: TeamMemberType,
        selector: suspend TeamMemberWithRoles.() -> Long?,
    ): TeamMember {
        val res = database.teamMemberDao().getTeamMemberWithRoles(name = filterName)
        val id = res?.selector() ?: filterParamId!!
        Log.d(tag = TAG) { "tm: $type id=$id" }
        return TeamMember(teamMemberId = id, teamMemberType = type)
    }

    private suspend fun allTeamMembers(filterName: String): List<TeamMember> {
        val res = database.teamMemberDao().getTeamMemberWithRoles(name = filterName)
        return buildList {
            res?.dubbers?.firstOrNull()?.let { add(TeamMember(teamMemberId = it.dubberId, teamMemberType = TeamMemberType.VOICE)) }
            res?.translators?.firstOrNull()?.let { add(TeamMember(teamMemberId = it.translatorId, teamMemberType = TeamMemberType.TRANSLATOR)) }
            res?.soundEngineers?.firstOrNull()?.let { add(TeamMember(teamMemberId = it.soundEngineerId, teamMemberType = TeamMemberType.SOUND)) }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getTitlesByFilter(filter: Filter): Flow<PagingData<Anime>> = Pager(
        config = config,
        remoteMediator = AnimeRemoteMediator(
            api = api,
            database = database,
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
        )
    }.flow

    companion object Companion {
        private const val TAG: String = "SearchRepo"
    }

}

private fun List<DubberDbo>.toVoiceTeamMembers(): List<TeamMember> = map {
    TeamMember(teamMemberId = it.dubberId, teamMemberType = TeamMemberType.VOICE)
}

private fun List<TranslatorDbo>.toTranslatorTeamMembers(): List<TeamMember> = map {
    TeamMember(teamMemberId = it.translatorId, teamMemberType = TeamMemberType.TRANSLATOR)
}

private fun List<SoundEngineerDbo>.toSoundTeamMembers(): List<TeamMember> = map {
    TeamMember(teamMemberId = it.soundEngineerId, teamMemberType = TeamMemberType.SOUND)
}
