package id.anison.amanogawa.core.data.filter

import id.anison.amanogawa.core.network.model.GenreDto
import id.anison.amanogawa.core.database.genre.GenreDbo


data class Genre(
    val genreId: Long,
    val name: String,
    val botIdName: String,
)

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
