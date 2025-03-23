package id.anison.feature.search.model

import id.anison.data.search.DubersDto
import id.anison.data.search.FilterDto
import id.anison.data.genre.GenreDto

data class FilterUI(
    val id: Int = 0,
    val dubers: DubersUI = DubersUI(),
    var genres: List<GenreUI> = listOf(),
    val years: List<Short> = listOf(),
)

data class DubersUI(
    val s: Map<Long, String> = emptyMap(),
    val t: Map<Long, String> = emptyMap(),
    val v: Map<Long, String> = emptyMap(),
)

data class GenreUI(
    val genreId: Long,
    val name: String,
    val botIdName: String,
)

internal fun FilterDto.toFilterUI() = FilterUI(
    id = id,
    dubers = dubers.toDubersUI(),
    genres = genres.map { it.toGenreUI() },
    years = years
)

internal fun FilterUI.toFilterDto() = FilterDto(
    id = id,
    dubers = dubers.toDubersDto(),
    genres = genres.map { it.toGenreDto() },
    years = years
)

internal fun DubersDto.toDubersUI() = DubersUI(
    s = s,
    t = t,
    v = v,
)

internal fun DubersUI.toDubersDto() = DubersDto(
    s = s,
    t = t,
    v = v,
)

internal fun GenreDto.toGenreUI() = GenreUI(
    genreId = genreId,
    name = name,
    botIdName = botIdName,
)

internal fun GenreUI.toGenreDto() = GenreDto(
    genreId = genreId,
    name = name,
    botIdName = botIdName,
)