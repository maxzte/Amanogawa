package id.anison.amanogawa.core.network

import id.anison.amanogawa.core.network.model.AnimeIDsDto
import id.anison.amanogawa.core.network.model.FilterDto
import id.anison.amanogawa.core.network.model.TeamDto
import id.anison.amanogawa.core.network.model.anime.AnimeDetailsDto
import id.anison.amanogawa.core.network.model.anime.AnimeDto
import id.anison.amanogawa.core.network.model.anime.AnimeEpisodeDto
import id.anison.amanogawa.core.network.model.anime.AnimeEpisodesDto
import id.anison.amanogawa.core.network.model.anime.AnimeTitlesDto
import id.anison.amanogawa.core.network.model.anime.SearchResultDto
import id.anison.amanogawa.core.network.util.AmanogawaApiInterceptor
import id.anison.amanogawa.core.network.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.concurrent.TimeUnit


open class AmanogawaApiImpl() : AmanogawaApi {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AmanogawaApiInterceptor())
            .callTimeout(timeout = 15, unit = TimeUnit.SECONDS)
            .readTimeout(timeout = 15, unit = TimeUnit.SECONDS)
            .build()
    }
    private val lenientJson = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private fun executeRequest(request: Request): Response {
        return client.newCall(request).execute()
    }

    private inline fun <reified T> parseResponse(response: Response): T {
        val body = response.body.string()
        return lenientJson.decodeFromString<T>(body)
    }

    override fun baseUrl(): HttpUrl.Builder {
        return HttpUrl.Builder()
            .scheme("https")
            .host(Constants.HOST.url)
            .addPathSegment(Constants.API.url)
    }

    override suspend fun getAll(): Result<AnimeTitlesDto<AnimeDto>> {
        return try {
            val url = baseUrl().addPathSegment(Constants.TITLES.url).build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)

            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            val initialData = parseResponse<AnimeTitlesDto<AnimeDto>>(response)
            val allTitles = coroutineScope {
                (1..initialData.pages).map { page ->
                    async(Dispatchers.IO) {
                        val pageUrl = baseUrl().addPathSegment(Constants.TITLES.url)
                            .addQueryParameter("page", page.toString())
                            .build()
                        val pagedRequest = Request.Builder().url(pageUrl).build()
                        val pagedResponse = executeRequest(pagedRequest)

                        if (!pagedResponse.isSuccessful) {
                            throw Exception("Failed to load page $page: ${pagedResponse.code}")
                        }
                        parseResponse<AnimeTitlesDto<AnimeDto>>(pagedResponse).data
                    }
                }.awaitAll().flatten()
            }
            Result.success(AnimeTitlesDto(data = allTitles, pages = initialData.pages))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getTitlesByPage(page: Int): Result<AnimeTitlesDto<AnimeDto>> {
        return try {
            if (page <= 0) {
                return Result.failure(Exception("Page:${page} is not exist"))
            }
            val url = baseUrl().addPathSegment(Constants.TITLES.url)
                .addQueryParameter("page", page.toString())
                .build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            val responseData = parseResponse<AnimeTitlesDto<AnimeDto>>(response = response)
            if (page > responseData.pages) {
                return Result.failure(Exception("Page:${page} is not exist\t${response.code}"))
            }
            responseData.currentPage = page
            Result.success(responseData)
        } catch (e: IOException) {
            println(e.message)
            Result.failure(e)
        }
    }

    override suspend fun getCurrentTitles(): Result<List<AnimeDto>> {
        return try {
            val url = baseUrl()
                .addPathSegment(Constants.TITLES.url).addPathSegment(Constants.CURRENT.url)
                .build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<List<AnimeDto>>(response))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getAnime(id: Long): Result<AnimeDetailsDto> {
        return try {
            val url = baseUrl()
                .addPathSegment(Constants.TITLE.url)
                .addPathSegment(id.toString())
                .build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<AnimeDetailsDto>(response))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getAnimeEpisodes(id: Long): Result<AnimeEpisodesDto> {
        return try {
            fun buildEpisodesUrl(page: Int = 1): HttpUrl {
                return baseUrl()
                    .addPathSegment(Constants.EPISODES.url)
                    .addPathSegment(id.toString())
                    .apply {
                        if (page >= 1) addQueryParameter("page", page.toString())
                    }
                    .build()
            }

            fun fetchEpisodes(page: Int): Result<AnimeEpisodesDto> {
                val request = Request.Builder().url(buildEpisodesUrl(page)).build()
                val response = executeRequest(request)
                if (!response.isSuccessful) {
                    return Result.failure(Exception("Failed to load page $page: ${response.code}"))
                }
                return try {
                    Result.success(parseResponse<AnimeEpisodesDto>(response))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

            val episodes = mutableListOf<AnimeEpisodeDto>()
            val initialResponse = fetchEpisodes(1)
            val initialData = initialResponse.getOrThrow()

            episodes.addAll(initialData.data)
            val afterFirstPageEpisodes = (2..initialData.pages).mapNotNull { page ->
                fetchEpisodes(page).getOrThrow().data
            }.flatten()
            episodes.addAll(afterFirstPageEpisodes)
            Result.success(
                AnimeEpisodesDto(
                    pages = initialData.pages,
                    data = episodes,
                    botUrl = initialData.botUrl,
                )
            )
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getFilter(): Result<FilterDto> {
        return try {
            val url = baseUrl().addPathSegment(Constants.FILTERS.url).build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<FilterDto>(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFilteredTitles(
        page: Int,
        years: List<Short>?,
        genres: List<Long>?,
        voices: List<Long>?,
        sounds: List<Long>?,
        translators: List<Long>?,
    ): Result<AnimeTitlesDto<AnimeDto>> {
        return try {
            fun List<Any>.toQueryParam() =
                joinToString(prefix = "[", postfix = "]", separator = ",") { it.toString() }

            val params = mutableMapOf<String, String>().apply {
                years?.let { put("y", it.toQueryParam()) }
                genres?.let { put("g", it.toQueryParam()) }
                voices?.let { put("v", it.toQueryParam()) }
                sounds?.let { put("s", it.toQueryParam()) }
                translators?.let { put("t", it.toQueryParam()) }
            }
            val urlBuilder = StringBuilder(Constants.TITLES.url)
            params.forEach { (key, value) -> urlBuilder.append("$key=$value&") }
            val url = baseUrl()
                .addPathSegment(Constants.TITLES.url)
                .apply {
                    params.forEach { (key, value) -> addQueryParameter(key, value) }
                    addQueryParameter("page", page.toString())
                }
                .build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<AnimeTitlesDto<AnimeDto>>(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRandomAnime(): Result<Long> {
        return try {
            val url = baseUrl().addPathSegment(Constants.RANDOM.url).build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<List<String>>(response).first().trim().toLong())
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getCountAnimeAndTheirIDs(): Result<AnimeIDsDto> {
        return try {
            fun buildCountAnimeUrl(page: Int = 1): HttpUrl {
                return baseUrl()
                    .addPathSegment(Constants.TITLES.url)
                    .apply {
                        if (page > 1) addQueryParameter("page", page.toString())
                    }
                    .build()
            }

            val firstRequest = Request.Builder().url(buildCountAnimeUrl()).build()
            val firstResponse = executeRequest(firstRequest)
            if (!firstResponse.isSuccessful) {
                return Result.failure(Exception("Request failed: ${firstResponse.code}"))
            }

            val firstPageData = parseResponse<AnimeTitlesDto<AnimeDto>>(firstResponse)
            val ids = mutableListOf<Long>()
            ids.addAll(firstPageData.data.map { it.animeId })
            for (page in 2..firstPageData.pages) {
                val pagedResponse = getTitlesByPage(page)
                if (!pagedResponse.isSuccess) {
                    return Result.failure(Exception("Failed to load page $page: ${pagedResponse.getOrThrow()}"))
                }
                val pagedData = pagedResponse.getOrThrow().data

                ids.addAll(pagedData.map { it.animeId })
            }
            Result.success(AnimeIDsDto(ids, ids.size))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }


    override suspend fun searchAnimeByName(name: String): Result<List<SearchResultDto>> {
        return try {
            val url = baseUrl().addPathSegment(Constants.SEARCH.url)
                .addQueryParameter("s", "\"$name\"").build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<List<SearchResultDto>>(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTeam(): Result<List<TeamDto>> {
        return try {
            val url = baseUrl().addPathSegment(Constants.TEAM.url).build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<List<TeamDto>>(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}