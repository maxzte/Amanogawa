package id.anison.amanogawa.core.data.filter

import androidx.room.Transaction
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.data.toAppError
import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.database.filter.FilterDao
import id.anison.amanogawa.core.database.filter.FilterDbo
import id.anison.amanogawa.core.database.filter.FilterDubberCrossRef
import id.anison.amanogawa.core.database.filter.FilterGenreCrossRef
import id.anison.amanogawa.core.database.filter.FilterSoundEngineerCrossRef
import id.anison.amanogawa.core.database.filter.FilterTranslatorCrossRef
import id.anison.amanogawa.core.database.filter.FilterTypeDbo
import id.anison.amanogawa.core.database.filter.FilterYearDbo
import id.anison.amanogawa.core.database.genre.GenreDao
import id.anison.amanogawa.core.database.genre.GenreDbo
import id.anison.amanogawa.core.database.teammember.DubberDbo
import id.anison.amanogawa.core.database.teammember.SoundEngineerDbo
import id.anison.amanogawa.core.database.teammember.TeamMemberDao
import id.anison.amanogawa.core.database.teammember.TeamMemberDbo
import id.anison.amanogawa.core.database.teammember.TranslatorDbo
import id.anison.amanogawa.core.domain.model.Filter
import id.anison.amanogawa.core.domain.model.FilterType
import id.anison.amanogawa.core.domain.model.Genre
import id.anison.amanogawa.core.domain.model.TeamMember
import id.anison.amanogawa.core.domain.model.TeamMemberType
import id.anison.amanogawa.core.domain.repository.FilterRepo
import id.anison.amanogawa.core.network.AmanogawaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

@Single
class FilterRepoImpl(
    private val api: AmanogawaApi,
    database: AmanogawaDatabase,
) : FilterRepo {
    private val filterDao: FilterDao = database.filterDao()
    private val genreDao: GenreDao = database.genreDao()
    private val teamMemberDao: TeamMemberDao = database.teamMemberDao()

    @Transaction
    override suspend fun updateFilter(): DataState<Filter> = withContext(Dispatchers.IO) {
        api.getFilter().fold(
            onSuccess = { filterDto ->
                val filter = filterDto.toFilter()
                filterDao.insertFilter(filter = filter.toFilterDbo())
                saveFilterContents(
                    filterType = FilterTypeDbo.DISPLAYED,
                    genres = filter.genres,
                    teamMembers = filter.teamMembers,
                    years = filter.years,
                )
                DataState.Success(data = filter)
            },
            onFailure = { e ->
                Log.e(tag = TAG) { "Error loading filters: ${e.message}" }
                DataState.Error(exception = e.toAppError())
            }
        )
    }

    @Transaction
    override suspend fun saveSearchFilter(filter: Filter) = withContext(Dispatchers.IO) {
        filterDao.insertFilter(FilterDbo(FilterTypeDbo.SEARCH))
        saveFilterContents(
            filterType = FilterTypeDbo.SEARCH,
            genres = filter.genres,
            teamMembers = filter.teamMembers,
            years = filter.years,
        )
    }

    private suspend fun saveFilterContents(
        filterType: FilterTypeDbo,
        genres: List<Genre>,
        teamMembers: List<TeamMember>,
        years: List<Short>,
    ) {
        filterDao.deleteGenreRefs(filterType)
        filterDao.deleteDubberRefs(filterType)
        filterDao.deleteTranslatorRefs(filterType)
        filterDao.deleteSoundEngineerRefs(filterType)
        filterDao.deleteYears(filterType)

        insertGenres(genres = genres.map { it.toGenreDbo() })
        insertTeamMembers(teamMembers = teamMembers)

        insertGenreRefs(genres = genres, filterType = filterType)
        insertTeamMemberRefs(teamMembers = teamMembers, filterType = filterType)
        insertYears(years = years, filterType = filterType)
    }

    override suspend fun isExist(filterType: FilterType): Boolean = withContext(Dispatchers.IO) {
        val filterDbo = filterDao.getFilter(filterType.toFilterTypeDbo())
        return@withContext filterDbo != null
    }

    private suspend fun insertGenres(genres: List<GenreDbo>) {
        genreDao.insertGenres(genres)
    }

    @Transaction
    private suspend fun insertTeamMembers(teamMembers: List<TeamMember>): List<Long> {
        val distinct = teamMembers.distinctBy { it.name }
        Log.d(tag = TAG) { "distinct: $distinct" }
        teamMemberDao.insertTeamMembers(
            teamMembers = distinct.map { TeamMemberDbo(name = it.name) }
        )

        val allByName = teamMemberDao.getTeamMembersByNames(distinct.map { it.name })
        val memberMap = allByName.associateBy { it.name }
        val grouped = teamMembers.groupBy { it.teamMemberType }

        grouped[TeamMemberType.VOICE]?.let { list ->
            val dubbers = list.mapNotNull { teamMember ->
                memberMap[teamMember.name]?.let {
                    DubberDbo(
                        dubberId = teamMember.teamMemberId,
                        teamMemberId = it.teamMemberId,
                    )
                }
            }
            Log.d(tag = TAG) { "dubbers: $dubbers" }
            teamMemberDao.insertDubbers(dubbers)
        }

        grouped[TeamMemberType.TRANSLATOR]?.let { list ->
            val translators = list.mapNotNull { teamMember ->
                memberMap[teamMember.name]?.let {
                    TranslatorDbo(
                        translatorId = teamMember.teamMemberId,
                        teamMemberId = it.teamMemberId,
                    )
                }
            }
            Log.d(tag = TAG) { "translators: $translators" }
            teamMemberDao.insertTranslators(translators)
        }

        grouped[TeamMemberType.SOUND]?.let { list ->
            val engineers = list.mapNotNull { teamMember ->
                memberMap[teamMember.name]?.let {
                    SoundEngineerDbo(
                        soundEngineerId = teamMember.teamMemberId,
                        teamMemberId = it.teamMemberId,
                    )
                }
            }
            Log.d(tag = TAG) { "sounds: $engineers" }
            teamMemberDao.insertSoundEngineers(engineers)
        }

        return allByName.map { it.teamMemberId }
    }

    private suspend fun insertGenreRefs(genres: List<Genre>, filterType: FilterTypeDbo) {
        filterDao.insertGenreRefs(
            refs = genres.map {
                FilterGenreCrossRef(
                    filterType = filterType,
                    genreId = it.genreId,
                )
            }
        )
    }

    private suspend fun insertTeamMemberRefs(
        teamMembers: List<TeamMember>,
        filterType: FilterTypeDbo
    ) {
        val grouped = teamMembers.groupBy { it.teamMemberType }

        grouped[TeamMemberType.VOICE]?.let { list ->
            filterDao.insertDubberRefs(
                refs = list.map {
                    FilterDubberCrossRef(
                        filterType = filterType,
                        dubberId = it.teamMemberId,
                    )
                }
            )
        }

        grouped[TeamMemberType.TRANSLATOR]?.let { list ->
            filterDao.insertTranslatorRefs(
                refs = list.map {
                    FilterTranslatorCrossRef(
                        filterType = filterType,
                        translatorId = it.teamMemberId,
                    )
                }
            )
        }

        grouped[TeamMemberType.SOUND]?.let { list ->
            filterDao.insertSoundEngineerRefs(
                refs = list.map {
                    FilterSoundEngineerCrossRef(
                        filterType = filterType,
                        soundEngineerId = it.teamMemberId,
                    )
                }
            )
        }
    }

    private suspend fun insertYears(years: List<Short>, filterType: FilterTypeDbo) {
        filterDao.insertYears(
            years = years.map {
                FilterYearDbo(
                    filterType = filterType,
                    year = it,
                )
            }
        )
    }

    companion object {
        private const val TAG = "FilterRepo"
    }

}
