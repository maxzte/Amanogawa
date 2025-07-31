package id.anison.amanogawa.core.data.filter

import id.anison.amanogawa.core.database.filter.FilterDbo
import id.anison.amanogawa.core.database.filter.FilterTypeDbo
import id.anison.amanogawa.core.database.genre.GenreDbo
import id.anison.amanogawa.core.domain.model.Filter
import id.anison.amanogawa.core.domain.model.FilterType
import id.anison.amanogawa.core.domain.model.Genre
import id.anison.amanogawa.core.domain.model.TeamMember
import id.anison.amanogawa.core.domain.model.TeamMemberType
import id.anison.amanogawa.core.network.model.FilterDto
import id.anison.amanogawa.core.network.model.GenreDto
import id.anison.amanogawa.core.network.model.TeamMemberDto
import id.anison.amanogawa.core.network.model.TeamMemberTypeDto

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

internal fun TeamMemberDto.toTeamMember() = TeamMember(
    teamMemberId = teamMemberId,
    name = name,
    teamMemberType = teamMemberType.toTeamMemberType()
)

internal fun TeamMemberTypeDto.toTeamMemberType() = TeamMemberType.valueOf(this.name)

internal fun GenreDto.toGenre() = Genre(
    genreId = genreId,
    name = name,
    botIdName = botIdName!!,
)

internal fun GenreDbo.toGenre() = Genre(
    genreId = genreId,
    name = name,
    botIdName = botIdName,
)

internal fun Genre.toGenreDbo() = GenreDbo(
    genreId = genreId,
    name = name,
    botIdName = botIdName,
)