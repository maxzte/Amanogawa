package id.anison.data.genre

import id.anison.api.model.Genre
import id.anison.database.genre.GenreDbo


data class GenreDto(
    val genreId: Long,
    val name: String,
    val botIdName: String,
)

internal fun Genre.toGenreDto() = GenreDto(
    genreId = genreId,
    name = name,
    botIdName = botIdName,
)

internal fun GenreDbo.toGenreDto() = GenreDto(
    genreId = genreId,
    name = name,
    botIdName = botIdName,
)

internal fun GenreDto.toGenreDbo() = GenreDbo(
    genreId = genreId,
    name = name,
    botIdName = botIdName,
)

