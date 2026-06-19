package id.anison.amanogawa.core.network

import de.jensklingenberg.ktorfit.Ktorfit
import id.anison.amanogawa.core.network.model.FilterDto
import id.anison.amanogawa.core.network.model.TeamDto
import id.anison.amanogawa.core.network.model.anime.AnimeDetailsDto
import id.anison.amanogawa.core.network.model.anime.AnimeDto
import id.anison.amanogawa.core.network.model.anime.AnimeEpisodesDto
import id.anison.amanogawa.core.network.model.anime.AnimeTitlesDto
import id.anison.amanogawa.core.network.model.anime.SearchResultDto
import id.anison.amanogawa.core.network.util.AmanogawaApiInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

open class AmanogawaApiImpl(
    baseUrl: String = "https://amanogawa.space/api/"
) : AmanogawaApi {

    private val client = HttpClient(OkHttp) {
        engine {
            addInterceptor(AmanogawaApiInterceptor())
            config {
                callTimeout(15, TimeUnit.SECONDS)
                readTimeout(15, TimeUnit.SECONDS)
            }
        }
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    private val service = Ktorfit.Builder()
        .baseUrl(baseUrl)
        .httpClient(client)
        .build()
        .createAmanogawaService()

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
