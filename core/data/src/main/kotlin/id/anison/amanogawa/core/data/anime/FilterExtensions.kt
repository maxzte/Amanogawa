package id.anison.amanogawa.core.data.anime

import id.anison.amanogawa.core.domain.model.Filter
import id.anison.amanogawa.core.domain.model.TeamMemberType

data class FilterParams(
    val voices: List<Long>?,
    val sounds: List<Long>?,
    val translators: List<Long>?,
    val genres: List<Long>?,
    val years: List<Short>?,
)

internal fun Filter.toFilterParams() = FilterParams(
    voices = teamMembers.filter { it.teamMemberType == TeamMemberType.VOICE }
        .map { it.teamMemberId }.takeIf { it.isNotEmpty() },
    sounds = teamMembers.filter { it.teamMemberType == TeamMemberType.SOUND }
        .map { it.teamMemberId }.takeIf { it.isNotEmpty() },
    translators = teamMembers.filter { it.teamMemberType == TeamMemberType.TRANSLATOR }
        .map { it.teamMemberId }.takeIf { it.isNotEmpty() },
    genres = genres.map { it.genreId }.takeIf { it.isNotEmpty() },
    years = years.takeIf { it.isNotEmpty() },
)
