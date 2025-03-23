package id.anison.data.anime

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import id.anison.api.AmanogawaApi
import id.anison.common.AndroidLogcatLogger
import id.anison.data.ReqRes
import id.anison.data.settings.config
import id.anison.database.AmanogawaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class AnimeRepo(
    private val database: AmanogawaDatabase,
    private val api: AmanogawaApi,
    private val log: AndroidLogcatLogger,
) {
    private val animeDao = database.animeDao()
    private val animeDetailsDao = database.animeDetailsDao()
    private val favoriteDao = database.favoriteDao()
    companion object {
        const val TAG: String = "Anime repo"
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getAllWithPagination(): Flow<PagingData<AnimeDto>> = Pager(
        config = config,
        remoteMediator = AnimeRemoteMediator(api, database, log),
        pagingSourceFactory = {
            animeDao.pagingAll()
        },
    ).flow.map { pd -> pd.map { animeDbo -> animeDbo.toAnimeDto() } }

    fun getCurrentAnime(): Flow<ReqRes<List<AnimeDto>>> = flow {
        emit(ReqRes.InProgress())

        api.getCurrentTitles().fold(
            onSuccess = { animeData ->
                emit(ReqRes.Success(animeData.map { anime -> anime.toAnimeDto() }))
            },
            onFailure = {
                emit(ReqRes.Error(error = it.cause))
            }
        )
    }.flowOn(Dispatchers.IO)

    fun getAnime(id: Long): Flow<ReqRes<AnimeDetailsDto>> = flow {
        emit(ReqRes.InProgress())

        val isFav = favoriteDao.isFavorite(id).firstOrNull() == true
        var localData: AnimeDetailsDto? = null
        if (isFav) {
            log.d(TAG, "ANIME WITH id: $id is favorite")
            val animeDbo = animeDao.getAnimeById(id)
            val animeDetailsDbo = animeDetailsDao.getAnimeDetailsById(id)

            localData = if (animeDbo != null && animeDetailsDbo != null) {
                animeDetailsDbo.toAnimeDetailsDto(animeDbo.toAnimeDto())
            } else null

            localData?.let {
                emit(ReqRes.Success(it))
            }
        }

        if (localData?.animeDto?.isCurrent == true || !isFav) {
            log.d(TAG, "ANIME WITH id: $id is current OR ISN`T FAVORITE")

            api.getAnime(id).fold(
                onSuccess = { anime ->
                    val animeDetailsDto = anime.toAnimeDetailsDto()
                    if (isFav && localData != null && !animeDetailsDto.isSame(localData)) {
                        log.d(TAG, "SAVE ANIME WITH id: $id")
                        saveAnime(anime = animeDetailsDto)
                    }
                    emit(ReqRes.Success(animeDetailsDto))
                },
                onFailure = { e ->
                    log.d(TAG, "API request failed: $e")
                    emit(ReqRes.Error(null, e))
                }
            )
        }
    }.flowOn(Dispatchers.IO)


    suspend fun getAnimeEpisodes(id: Long): ReqRes<EpisodesDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getAnimeEpisodes(id)
                response.fold(
                    onSuccess = { episodes ->
                        ReqRes.Success(episodes.toEpisodesDto())
                    },
                    onFailure = { e ->
                        ReqRes.Error(response.getOrThrow().toEpisodesDto(), e)
                    }
                )
            } catch (e: Exception) {
                log.d(TAG, "EXCEPTION while getAnimeEpisodes $e")
                ReqRes.Error(null, e)
            }
        }
    }

    suspend fun saveAnime(anime: AnimeDetailsDto) {
        database.withTransaction {
            animeDao.insertAnime(anime.animeDto.toAnimeDbo())
            animeDetailsDao.saveAnimeDetails(anime.toAnimeDetailsDbo())
        }
    }

    suspend fun deleteAnimeDetails(animeDetails: AnimeDetailsDto) {
        animeDetailsDao.deleteAnimeDetails(animeDetails = animeDetails.toAnimeDetailsDbo())
    }
}