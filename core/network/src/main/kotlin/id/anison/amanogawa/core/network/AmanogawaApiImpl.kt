package id.anison.amanogawa.core.network

import id.anison.amanogawa.core.network.model.FilterDto
import id.anison.amanogawa.core.network.model.TeamDto
import id.anison.amanogawa.core.network.model.anime.AnimeDetailsDto
import id.anison.amanogawa.core.network.model.anime.AnimeDto
import id.anison.amanogawa.core.network.model.anime.AnimeEpisodesDto
import id.anison.amanogawa.core.network.model.anime.AnimeTitlesDto
import id.anison.amanogawa.core.network.model.anime.SearchResultDto
import id.anison.amanogawa.core.network.util.PageDto
import id.anison.amanogawa.core.network.util.fetchAllPages
import id.anison.amanogawa.core.network.util.toQueryParam
import kotlinx.coroutines.CancellationException
import org.koin.core.annotation.Single

@Single
open class AmanogawaApiImpl(
    private val service: AmanogawaService
) : AmanogawaApi {

    private suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> = try {
        Result.success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getAll(): Result<AnimeTitlesDto<AnimeDto>> = safeApiCall {
        val initialData = service.getTitlesByPage(1)
        val allData = fetchAllPages(
            firstPage = PageDto(pages = initialData.pages, data = initialData.data),
            fetcher = { page -> service.getTitlesByPage(page).data }
        )
        initialData.copy(data = allData)
    }

    override suspend fun getTitlesByPage(page: Int): Result<AnimeTitlesDto<AnimeDto>> = safeApiCall {
        if (page <= 0) throw Exception("Page $page does not exist")
        val titles = service.getTitlesByPage(page)
        if (page > titles.pages) throw Exception("Page $page does not exist")
        titles.copy(currentPage = page)
    }

    override suspend fun getCurrentTitles(): Result<List<AnimeDto>> = safeApiCall {
        service.getCurrentTitles()
    }

    override suspend fun getAnime(id: Long): Result<AnimeDetailsDto> = safeApiCall {
        service.getAnime(id)
    }

    override suspend fun getAnimeEpisodes(id: Long): Result<AnimeEpisodesDto> = safeApiCall {
        val initialData = service.getAnimeEpisodes(id, 1)
        val allData = fetchAllPages(
            firstPage = PageDto(pages = initialData.pages, data = initialData.data),
            fetcher = { page -> service.getAnimeEpisodes(id, page).data }
        )
        initialData.copy(data = allData)
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
        val result = service.getRandomAnime()
        result.firstOrNull()
            ?.trim()
            ?.toLongOrNull()
            ?: throw Exception("Invalid random anime response: $result")
    }

    override suspend fun searchAnimeByName(name: String): Result<List<SearchResultDto>> = safeApiCall {
        service.searchAnimeByName("\"$name\"")
    }

    override suspend fun getTeam(): Result<List<TeamDto>> = safeApiCall {
        service.getTeam()
    }
}
