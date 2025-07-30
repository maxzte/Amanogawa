package id.anison.amanogawa.core.domain

import id.anison.amanogawa.core.data.filter.Filter
import id.anison.amanogawa.core.data.filter.FilterType
import id.anison.amanogawa.core.data.filter.Genre
import id.anison.amanogawa.core.data.filter.TeamMember
import id.anison.amanogawa.core.data.filter.TeamMemberType
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.model.FilterUI
import id.anison.amanogawa.core.model.GenreUI
import id.anison.amanogawa.core.model.TeamMemberTypeUI
import id.anison.amanogawa.core.model.TeamMemberUI

fun Filter.toFilterUI() = FilterUI(
    filterTypeUI = filterType.toFilterTypeUI(),
    genres = genres.map { it.toGenreUI() },
    teamMembers = teamMembers.map { it.toTeamMemberUI() },
    years = years,
)

fun FilterType.toFilterTypeUI() = FilterTypeUI.valueOf(this.name)

fun FilterUI.toFilter() = Filter(
    filterType = FilterType.SEARCH,
    teamMembers = teamMembers.map { it.toTeamMember() },
    genres = genres.map { it.toGenre() },
    years = years,
)

fun Genre.toGenreUI() = GenreUI(
    genreId = genreId,
    name = name,
    botIdName = botIdName,
)

fun GenreUI.toGenre() = Genre(
    genreId = genreId,
    name = name,
    botIdName = botIdName,
)

fun TeamMember.toTeamMemberUI() = TeamMemberUI(
    teamMemberId = teamMemberId,
    name = name,
    teamMemberType = TeamMemberTypeUI.valueOf(teamMemberType.name)
)

fun TeamMemberUI.toTeamMember() = TeamMember(
    teamMemberId = teamMemberId,
    name = name,
    teamMemberType = TeamMemberType.valueOf(teamMemberType.name)
)
