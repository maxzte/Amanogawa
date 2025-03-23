package id.anison.data.genre

import id.anison.common.AndroidLogcatLogger
import id.anison.database.AmanogawaDatabase

class GenreRepo(
    private val database: AmanogawaDatabase,
    private val log: AndroidLogcatLogger,
) {

    private val genreDao = database.genreDao()

    suspend fun saveGenres(genres: List<GenreDto>) {
        genreDao.insertGenres(genres.map { it.toGenreDbo() })
    }
}