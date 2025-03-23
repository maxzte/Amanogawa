package id.anison.data.search

import id.anison.api.model.Dubers
import id.anison.api.model.Filter
import id.anison.data.genre.GenreDto
import id.anison.data.genre.toGenreDbo
import id.anison.data.genre.toGenreDto
import id.anison.database.filter.DubersDbo
import id.anison.database.filter.FilterDbo


data class FilterDto(
    val id: Int = 0,
    val dubers: DubersDto = DubersDto(),
    val genres: List<GenreDto> = listOf(),
    val years: List<Short> = listOf(),
)

data class DubersDto(
    val s: Map<Long, String> = emptyMap(),
    val t: Map<Long, String> = emptyMap(),
    val v: Map<Long, String> = emptyMap(),
)

internal fun Filter.toFilterDto() = FilterDto(
    dubers = dubers.toDubersDto(),
    genres = genres.map { it.toGenreDto() },
    years = years,
)

internal fun Filter.toFilterDbo() = FilterDbo(
    dubers = dubers.toDubersDbo(),
    genres = genres.map { it.toGenreDto().toGenreDbo() },
    years = years,
)

internal fun FilterDbo.toFilterDto() = FilterDto(
    dubers = dubers.toDubersDto(),
    genres = genres.map { it.toGenreDto() },
    years = years,
)

internal fun Dubers.toDubersDto() = DubersDto(
    s = s,
    t = t,
    v = v,
)

internal fun Dubers.toDubersDbo() = DubersDbo(
    s = s,
    t = t,
    v = v,
)

internal fun DubersDbo.toDubersDto() = DubersDto(
    s = s,
    t = t,
    v = v,
)

internal fun DubersDto.toDubersDbo() = DubersDbo(
    s = s,
    t = t,
    v = v,
)