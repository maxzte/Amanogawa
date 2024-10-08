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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.concurrent.TimeUnit

interface AmanogawaApi {
    suspend fun getAll(): Result<List<Title>>
    suspend fun getTitles(page: Int): Result<AnimeTitles<Title>>
    suspend fun getCurrentTitles(): Result<List<CurrentTitle>>
    suspend fun getAnime(id: Long): Result<Anime>
    suspend fun getAnimeEpisodes(id: Long): Result<AnimeEpisodes<Episode>>
    suspend fun getFilter(): Result<Filter>
    suspend fun getFilteredTitles(
        page: Int,
        years: List<Int>?,
        genres: List<Int>?,
        voices: List<Int>?,
        sounds: List<Int>?,
        translators: List<Int>?,
    ): Result<AnimeTitles<Title>>

    suspend fun getRandomAnime(): Result<Long>
    suspend fun getCountAnimeAndTheirIDs(): Result<AnimeIDs>
    suspend fun getFoundAnime(name: String): Result<List<FoundAnime>>
    suspend fun getTeam(): Result<List<TeamItem>>
}

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

    private suspend fun executeRequest(request: Request): Response {
        return client.newCall(request).execute()//.executeAsync()
    }

    override suspend fun getAll(): Result<List<Title>> {
        val allTitles = mutableListOf<Title>()
        val url = Constants.TITLES.url
        val request = Request.Builder().url(url).build()
        val response = executeRequest(request)

        if (response.isSuccessful) {
            val json = response.body.string()
            val res = json.let { lenientJson.decodeFromString<AnimeTitles<Title>>(it) }
            for (page in 1..res.pages) {
                val pagedUrl = "${Constants.TITLES.url}page=" + page.toString()
                val pagedRequest = Request.Builder().url(pagedUrl).build()
                val pagedResponse = executeRequest(pagedRequest)

                if (pagedResponse.isSuccessful) {
                    val pagedJson = pagedResponse.body.string()
                    val pagedRes =
                        pagedJson.let { lenientJson.decodeFromString<AnimeTitles<Title>>(it) }
                    allTitles.addAll(pagedRes.data)
                } else {
                    return Result.failure(Exception("Failed to load page $page: ${pagedResponse.code}"))
                }
            }
            return Result.success(allTitles.toList())
        } else return Result.failure(Exception("Failure ${response.request}"))
    }

    override suspend fun getTitles(page: Int): Result<AnimeTitles<Title>> {
        val url = "${Constants.TITLES.url}page=" + page.toString()
        val request = Request.Builder().url(url).build()
        val response = executeRequest(request)

        if (response.isSuccessful) {
            val json = response.body.string()
            val res = json.let { lenientJson.decodeFromString<AnimeTitles<Title>>(it) }
            if (page < 0 || page > res.pages)
                return Result.success(AnimeTitles(pages = res.pages, data = emptyList()))
            return Result.success(res)
        } else return Result.failure(Exception("${response.code}"))
    }

    override suspend fun getCurrentTitles(): Result<List<CurrentTitle>> {
        val request = Request.Builder().url(Constants.CURRENT.url).build()
        val response = executeRequest(request)

        if (response.isSuccessful) {
            val json = response.body.string()
            val res = json.let { lenientJson.decodeFromString<List<CurrentTitle>>(it) }

            return Result.success(res)
        } else return Result.failure(Exception("${response.code}"))
    }

    override suspend fun getAnime(id: Long): Result<Anime> {
        val request = Request.Builder().url(Constants.ANIME.url + id.toString()).build()
        try {
            val response = executeRequest(request)
            if (response.isSuccessful) {
                val json = response.body.string()
                val res = json.let { lenientJson.decodeFromString<Anime>(it) }

                return Result.success(res)
            } else return Result.failure(Exception("${response.code}"))
        } catch (e: IOException) {
            return Result.failure(e)
        }
    }

    override suspend fun getAnimeEpisodes(id: Long): Result<AnimeEpisodes<Episode>> {
        val url = "${Constants.ANIME_EPISODES.url}/$id"
        val request = Request.Builder().url(url).build()
        val response = executeRequest(request)

        if (response.isSuccessful) {
            val json = response.body.string()
            val res = json.let { lenientJson.decodeFromString<AnimeEpisodes<Episode>>(it) }
            val episodes: MutableList<Episode> = mutableListOf()
            for (page in 1..res.pages) {
                val pagedUrl = "${Constants.ANIME_EPISODES.url}/$id?page=$page"
                val pagedRequest = Request.Builder().url(pagedUrl).build()
                val pagedResponse = executeRequest(pagedRequest)

                if (pagedResponse.isSuccessful) {
                    val pagedJson = pagedResponse.body.string()
                    val pagedRes =
                        pagedJson.let { lenientJson.decodeFromString<AnimeEpisodes<Episode>>(it) }
                    episodes.addAll(pagedRes.data)
                } else {
                    return Result.failure(Exception("Failed to load page $page: ${pagedResponse.code}"))
                }
            }
            val finalResult = Result.success(
                AnimeEpisodes<Episode>(
                    pages = res.pages,
                    data = episodes,
                    bot_url = res.bot_url,
                )
            )
            return finalResult
        } else return Result.failure(Exception("${response.code}"))
    }

    override suspend fun getFilter(): Result<Filter> {
        val request = Request.Builder().url(Constants.FILTER.url).build()
        val response = executeRequest(request)

        if (response.isSuccessful) {
            val json = response.body.string()
            val res = json.let { lenientJson.decodeFromString<Filter>(it) }

            return Result.success(res)
        } else return Result.failure(Exception("${response.code}"))
    }

    override suspend fun getFilteredTitles(
        page: Int,
        years: List<Int>?,
        genres: List<Int>?,
        voices: List<Int>?,
        sounds: List<Int>?,
        translators: List<Int>?,
    ): Result<AnimeTitles<Title>> {
        val params = mutableMapOf<String, String>()

        years?.let {
            params["y"] =
                it.joinToString(prefix = "[", postfix = "]", separator = ",") { y -> y.toString() }
        }
        genres?.let {
            params["g"] =
                it.joinToString(prefix = "[", postfix = "]", separator = ",") { g -> g.toString() }
        }
        voices?.let {
            params["v"] =
                it.joinToString(prefix = "[", postfix = "]", separator = ",") { v -> v.toString() }
        }
        sounds?.let {
            params["s"] =
                it.joinToString(prefix = "[", postfix = "]", separator = ",") { s -> s.toString() }
        }
        translators?.let {
            params["t"] =
                it.joinToString(prefix = "[", postfix = "]", separator = ",") { t -> t.toString() }
        }

        val urlBuilder = StringBuilder(Constants.TITLES.url)
        params.forEach { (key, value) -> urlBuilder.append("$key=$value&") }
        val url = urlBuilder.toString() + "page=$page"
        val request = Request.Builder().url(url).build()
        val response = executeRequest(request)

        if (response.isSuccessful) {
            val json = response.body.string()
            val res = json.let { lenientJson.decodeFromString<AnimeTitles<Title>>(it) }

            return Result.success(res)
        } else return Result.failure(Exception("${response.code}"))
    }

    override suspend fun getRandomAnime(): Result<Long> {
        val request = Request.Builder().url(Constants.RANDOM_ANIME.url).build()
        val response = executeRequest(request)

        if (response.isSuccessful) {
            val json = response.body.string()
            val animeStr = json.let { lenientJson.decodeFromString<List<String>>(it) }
            val animeId = animeStr.first().trim()

            return Result.success(animeId.toLong())
        } else return Result.failure(Exception("${response.code}"))
    }

    override suspend fun getCountAnimeAndTheirIDs(): Result<AnimeIDs> {
        val url = Constants.TITLES.url
        val request = Request.Builder().url(url).build()
        val response = executeRequest(request)

        if (response.isSuccessful) {
            val json = response.body.string()
            val res = json.let { lenientJson.decodeFromString<AnimeTitles<Title>>(it) }
            var count = 0
            val listIds = mutableListOf<Long>()
            for (page in 1..res.pages) {
                val pagedUrl = "${Constants.TITLES.url}page=" + page.toString()
                val pagedRequest = Request.Builder().url(pagedUrl).build()
                val pagedResponse = executeRequest(pagedRequest)

                if (pagedResponse.isSuccessful) {
                    val pagedJson = pagedResponse.body.string()
                    val pagedRes =
                        pagedJson.let { lenientJson.decodeFromString<AnimeTitles<Title>>(it) }
                    listIds.addAll(pagedRes.data.map { it.id })
                    count += pagedRes.data.size
                } else {
                    return Result.failure(Exception("${response.code}"))
                }
            }
            return Result.success(AnimeIDs(listIds, count))
        } else return Result.failure(Exception("${response.code}"))
    }

    override suspend fun getFoundAnime(name: String): Result<List<FoundAnime>> {
        val url = Constants.SEARCH_ANIME.url + "\"$name\""
        val request = Request.Builder().url(url).build()
        val response = executeRequest(request)

        if (response.isSuccessful) {
            val json = response.body.string()
            val res = json.let { lenientJson.decodeFromString<List<FoundAnime>>(it) }

            return Result.success(res)
        } else return Result.failure(Exception("${response.code}"))
    }

    override suspend fun getTeam(): Result<List<TeamItem>> {
        val request = Request.Builder().url(Constants.TEAM.url).build()
        val response = executeRequest(request)

        if (response.isSuccessful) {
            val json = response.body.string()
            val res = json.let { lenientJson.decodeFromString<List<TeamItem>>(it) }

            return Result.success(res)
        } else return Result.failure(Exception("${response.code}"))
    }
}