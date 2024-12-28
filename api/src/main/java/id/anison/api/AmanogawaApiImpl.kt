package id.anison.api

import id.anison.api.model.Anime
import id.anison.api.model.AnimeEpisodes
import id.anison.api.model.AnimeIDs
import id.anison.api.model.AnimeTitles
import id.anison.api.model.CurrentTitle
import id.anison.api.model.Episode
import id.anison.api.model.Filter
import id.anison.api.model.FoundAnime
import id.anison.api.model.TeamItem
import id.anison.api.model.Title
import id.anison.api.util.AmanogawaApiInterceptor
import id.anison.api.util.Constants
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.concurrent.TimeUnit


class AmanogawaApiImpl : AmanogawaApi {
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor(AmanogawaApiInterceptor())
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

    private fun baseUrl(): HttpUrl.Builder {
        return HttpUrl.Builder()
            .scheme("https")
            .host(Constants.HOST.url)
            .addPathSegment(Constants.API.url)
    }

    override suspend fun getAll(): Result<List<Title>> {
        return try {
            val url = baseUrl().addPathSegment(Constants.TITLES.url).build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)

            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            val initialData = parseResponse<AnimeTitles>(response)
            val allTitles = (1..initialData.pages).flatMap { page ->
                val url = baseUrl().addPathSegment(Constants.TITLES.url)
                    .addQueryParameter("page", page.toString())
                    .build()
                val pagedRequest = Request.Builder().url(url).build()
                val pagedResponse = executeRequest(pagedRequest)

                if (pagedResponse.isSuccessful) {
                    parseResponse<AnimeTitles>(pagedResponse).data
                } else {
                    throw Exception("Failed to load page $page: ${pagedResponse.code}")
                }
            }
            Result.success(allTitles.toList())
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getTitles(page: Int): Result<AnimeTitles> {
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
            val responseData = parseResponse<AnimeTitles>(response)
            if (page > responseData.pages) {
                return Result.failure(Exception("Page:${page} is not exist\t${response.code}"))
            }
            Result.success(responseData)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentTitles(): Result<List<CurrentTitle>> {
        return try {
            val url = baseUrl()
                .addPathSegment(Constants.TITLES.url).addPathSegment(Constants.CURRENT.url)
                .build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<List<CurrentTitle>>(response))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getAnime(id: Long): Result<Anime> {
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
            Result.success(parseResponse<Anime>(response))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getAnimeEpisodes(id: Long): Result<AnimeEpisodes<Episode>> {
        return try {
            fun buildEpisodesUrl(page: Int = 1): HttpUrl {
                return baseUrl()
                    .addPathSegment(Constants.EPISODES.url)
                    .addPathSegment(id.toString())
                    .apply {
                        if (page > 1) addQueryParameter("page", page.toString())
                    }
                    .build()
            }

            val request = Request.Builder().url(buildEpisodesUrl()).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            val responseData = parseResponse<AnimeEpisodes<Episode>>(response)
            val episodes = (2..responseData.pages).flatMap { page ->
                val pagedRequest = Request.Builder().url(buildEpisodesUrl(page)).build()
                val pagedResponse = executeRequest(pagedRequest)
                if (!pagedResponse.isSuccessful) {
                    return Result.failure(Exception("Failed to load page $page: ${pagedResponse.code}"))
                }
                parseResponse<AnimeEpisodes<Episode>>(pagedResponse).data
            }
            Result.success(
                AnimeEpisodes<Episode>(
                    pages = responseData.pages,
                    data = episodes,
                    bot_url = responseData.bot_url,
                )
            )
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    override suspend fun getFilter(): Result<Filter> {
        return try {
            val url = baseUrl().addPathSegment(Constants.FILTERS.url).build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<Filter>(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFilteredTitles(
        page: Int,
        years: List<Int>?,
        genres: List<Int>?,
        voices: List<Int>?,
        sounds: List<Int>?,
        translators: List<Int>?,
    ): Result<AnimeTitles> {
        return try {
            fun List<Int>.toQueryParam() =
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
            Result.success(parseResponse<AnimeTitles>(response))
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

    override suspend fun getCountAnimeAndTheirIDs(): Result<AnimeIDs> {
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

            val firstPageData = parseResponse<AnimeTitles>(firstResponse)
            val ids = mutableListOf<Long>()
            ids.addAll(firstPageData.data.map { it.id })
            for (page in 2..firstPageData.pages) {
                val pagedUrl = buildCountAnimeUrl(page)
                val pagedRequest = Request.Builder().url(pagedUrl).build()
                val pagedResponse = executeRequest(pagedRequest)
                if (!pagedResponse.isSuccessful) {
                    return Result.failure(Exception("Failed to load page $page: ${pagedResponse.code}"))
                }
                val pagedData = parseResponse<AnimeTitles>(pagedResponse)
                ids.addAll(pagedData.data.map { it.id })
            }
            Result.success(AnimeIDs(ids, ids.size))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }


    override suspend fun searchAnimeByName(name: String): Result<List<FoundAnime>> {
        return try {
            val url = baseUrl().addPathSegment(Constants.SEARCH.url)
                .addQueryParameter("s", "\"$name\"").build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<List<FoundAnime>>(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTeam(): Result<List<TeamItem>> {
        return try {
            val url = baseUrl().addPathSegment(Constants.TEAM.url).build()
            val request = Request.Builder().url(url).build()
            val response = executeRequest(request)
            if (!response.isSuccessful) {
                return Result.failure(Exception("Request failed: ${response.code}"))
            }
            Result.success(parseResponse<List<TeamItem>>(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}