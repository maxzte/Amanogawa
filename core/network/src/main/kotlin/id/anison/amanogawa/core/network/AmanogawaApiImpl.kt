package id.anison.amanogawa.core.network

import id.anison.amanogawa.core.network.model.FilterDto
import id.anison.amanogawa.core.network.model.TeamDto
import id.anison.amanogawa.core.network.model.anime.AnimeDetailsDto
import id.anison.amanogawa.core.network.model.anime.AnimeDto
import id.anison.amanogawa.core.network.model.anime.AnimeEpisodesDto
import id.anison.amanogawa.core.network.model.anime.AnimeTitlesDto
import id.anison.amanogawa.core.network.model.anime.SearchResultDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Single

@Single
open class AmanogawaApiImpl(
    private val service: AmanogawaService
) : AmanogawaApi {

    private suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> = try {
        Result.success(call())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getAll(): Result<AnimeTitlesDto<AnimeDto>> = safeApiCall {
        val initialData = service.getTitlesByPage(1)
        if (initialData.pages <= 1) return@safeApiCall initialData

        val otherPagesData = coroutineScope {
            (2..initialData.pages).map { page ->
                async(Dispatchers.IO) {
                    service.getTitlesByPage(page).data
                }
            }.awaitAll().flatten()
        }
        initialData.copy(data = initialData.data + otherPagesData)
    }

    override suspend fun getTitlesByPage(page: Int): Result<AnimeTitlesDto<AnimeDto>> = safeApiCall {
        if (page <= 0) throw Exception("Page:$page is not exist")
        service.getTitlesByPage(page).also {
            if (page > it.pages) throw Exception("Page:$page is not exist")
            it.currentPage = page
        }
    }

    override suspend fun getCurrentTitles(): Result<List<AnimeDto>> = safeApiCall {
        service.getCurrentTitles()
    }

    override suspend fun getAnime(id: Long): Result<AnimeDetailsDto> = safeApiCall {
        service.getAnime(id)
    }

    override suspend fun getAnimeEpisodes(id: Long): Result<AnimeEpisodesDto> = safeApiCall {
        val initialData = service.getAnimeEpisodes(id, 1)
        if (initialData.pages <= 1) return@safeApiCall initialData

        val otherPagesData = coroutineScope {
            (2..initialData.pages).map { page ->
                async(Dispatchers.IO) {
                    service.getAnimeEpisodes(id, page).data
                }
            }.awaitAll().flatten()
        }
        initialData.copy(data = initialData.data + otherPagesData)
    }

    override suspend fun getFilter(): Result<FilterDto> = safeApiCall {
        service.getFilter()
    }

    override suspend fun getFilteredTitles(
        page: Int,
        years: List<Short>?,
        genres: List<Long>?,
        voices: List<Long>?,
        sounds: List<Long>?,
        translators: List<Long>?,
    ): Result<AnimeTitlesDto<AnimeDto>> = safeApiCall {
        fun List<Any>.toQueryParam() = joinToString(prefix = "[", postfix = "]", separator = ",") { it.toString() }

        service.getFilteredTitles(
            page = page,
            years = years?.toQueryParam(),
            genres = genres?.toQueryParam(),
            voices = voices?.toQueryParam(),
            sounds = sounds?.toQueryParam(),
            translators = translators?.toQueryParam(),
        )
    }

    override suspend fun getRandomAnime(): Result<Long> = safeApiCall {
        service.getRandomAnime().first().trim().toLong()
    }

    override suspend fun searchAnimeByName(name: String): Result<List<SearchResultDto>> = safeApiCall {
        service.searchAnimeByName("\"$name\"")
    }

    override suspend fun getTeam(): Result<List<TeamDto>> = safeApiCall {
        service.getTeam()
    }
}
