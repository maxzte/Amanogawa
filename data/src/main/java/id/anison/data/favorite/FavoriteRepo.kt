package id.anison.data.favorite

import id.anison.common.AndroidLogcatLogger
import id.anison.data.ReqRes
import id.anison.data.anime.AnimeRepo
import id.anison.database.AmanogawaDatabase
import id.anison.database.favorite.FavoriteDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull

class FavoriteRepo(
    private val database: AmanogawaDatabase,
    private val log: AndroidLogcatLogger,
    private val animeRepo: AnimeRepo,
) {
    private val favoriteDao = database.favoriteDao()
    private val genreDao = database.genreDao()

    companion object {
        const val TAG: String = "Favorite repo"
    }

    suspend fun getFavoriteById(id: Long): ReqRes<FavoriteDto> {
        return try {
            val dbRequest = favoriteDao.getFavoriteById(id)
            log.d(TAG, "Getting one favorite from DB. Fav is: $dbRequest")
            if (dbRequest != null) ReqRes.Success(dbRequest.toFavoriteDto())
            else ReqRes.Error(null, null)
        } catch (e: Exception) {
            log.e(TAG, "Error getting data from database. Cause = $e")
            ReqRes.Error(null, e)
        }
    }

    fun getFavorites(): Flow<ReqRes<List<FavoriteDto>>> = flow {
        emit(ReqRes.InProgress())
        val dbRequest = favoriteDao.getAll()
        emitAll(
            dbRequest.map { list ->
                ReqRes.Success(list.map { it.toFavoriteDto() })
            }
        )
    }.catch { e ->
        log.e(TAG, "Error getting data from database. Cause = $e")
        emit(ReqRes.Error(error = e))
    }

    fun isFavorite(id: Long): Flow<Boolean> {
        return favoriteDao.isFavorite(id)
    }

    suspend fun addToFavorite(favorite: FavoriteDto) {
        log.d(TAG, "Adding favorite to DB. Fav is: $favorite")
        favoriteDao.insertFavorite(favorite.toFavoriteDbo())
    }

    suspend fun removeFromFavorite(favorite: FavoriteDto) {
        favoriteDao.deleteFavorite(favorite.toFavoriteDbo())
    }

    suspend fun removeGenresByFavoriteId(favoriteId: Long) {
        genreDao.deleteGenresByAnimeDetailsId(favoriteId)
    }

    suspend fun clearFavoriteList() {
        favoriteDao.deleteAllFavorites()
    }

    fun observeNewEpFromFavorites(): Flow<ReqRes<List<FavoriteDto>>> = flow {
        emit(ReqRes.InProgress())

        val localFavorites = favoriteDao.getAll().map { list ->
            list.map { it.toFavoriteDto() }.filter { it.isCurrent }
        }.firstOrNull() ?: emptyList()

        val remoteFavorites = localFavorites.mapNotNull { favorite ->
            animeRepo.getAnime(favorite.favoriteId)
                .firstOrNull()?.let { animeRes ->
                    if (animeRes is ReqRes.Success &&
                        favorite.episodes.toString() != animeRes.data.animeDto.episodeCount
                    ) {
                        favorite
                    } else null
                }
        }

        emit(ReqRes.Success(remoteFavorites))
    }.catch { e ->
        emit(ReqRes.Error(error = e))
    }.flowOn(Dispatchers.IO)


    suspend fun checkForNewEpisode(favoriteFromDB: FavoriteDto): Boolean {
        val newEpisode =
            animeRepo.getAnimeEpisodes(favoriteFromDB.favoriteId).data?.data?.lastOrNull()?.number
        if (newEpisode == null) {
            log.e(TAG, "Error getting episodes for ID: ${favoriteFromDB.favoriteId}")
            return false
        }
        return if (favoriteFromDB.episodes == newEpisode) {
            false
        } else {
            log.d(
                TAG,
                "from DB is ${favoriteFromDB.episodes}\nfrom fetch is $newEpisode"
            )
            favoriteDao.insertFavorite(
                FavoriteDbo(
                    isFavorite = favoriteFromDB.isFavorite,
                    isCurrent = favoriteFromDB.isCurrent,
                    favoriteId = favoriteFromDB.favoriteId,
                    name = favoriteFromDB.name,
                    enJpName = favoriteFromDB.enJpName,
                    season = favoriteFromDB.season,
                    part = favoriteFromDB.part,
                    year = favoriteFromDB.year,
                    poster = favoriteFromDB.poster,
                    views = favoriteFromDB.views,
                    liked = favoriteFromDB.liked,
                    disliked = favoriteFromDB.disliked,
                    episodes = newEpisode,
                    isNewEpisode = true,
                )
            )
            true
        }
    }
}