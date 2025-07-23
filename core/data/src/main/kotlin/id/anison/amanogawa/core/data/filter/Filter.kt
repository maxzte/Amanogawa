package id.anison.amanogawa.core.data.filter

import id.anison.amanogawa.core.network.model.FilterDto
import id.anison.amanogawa.core.database.filter.FilterDbo
import id.anison.amanogawa.core.database.filter.FilterTypeDbo


data class Filter(
    val filterType: FilterType = FilterType.DISPLAYED,
    val genres: List<Genre> = listOf(),
    val teamMembers: List<TeamMember> = listOf(),
    val years: List<Short> = listOf(),
)

enum class FilterType {
    GENRE,
    DUBBER,
    TRANSLATOR,
    SOUND_ENGINEER,
    ALL_TEAM_MEMBER,
    YEAR,
    DISPLAYED,
    SEARCH
}

internal fun FilterDto.toFilter() = Filter(
    filterType = FilterType.DISPLAYED,
    teamMembers = teamMembers.map { it.toTeamMember() },
    genres = genres.filter { !it.botIdName.isNullOrBlank() }.map { it.toGenre() },
    years = years,
)

internal fun Filter.toFilterDbo() = FilterDbo(
    filterType = filterType.toFilterTypeDbo()
)

internal fun FilterType.toFilterTypeDbo() = FilterTypeDbo.valueOf(this.name)

internal fun Filter.generateCacheKey(): String {
    val genreIds = genres.map { it.genreId }.sorted().joinToString("_")
    val voiceIds = teamMembers.filter { it.teamMemberType == TeamMemberType.VOICE }.map { it.teamMemberId }.sorted().joinToString("_")
    val soundIds = teamMembers.filter { it.teamMemberType == TeamMemberType.SOUND }.map { it.teamMemberId }.sorted().joinToString("_")
    val transIds = teamMembers.filter { it.teamMemberType == TeamMemberType.TRANSLATOR }.map { it.teamMemberId }.sorted().joinToString("_")
    val yearIds = years.sorted().joinToString("_")

    return "g=$genreIds&y=$yearIds&v=$voiceIds&s=$soundIds&t=$transIds"
}
