package id.anison.amanogawa.core.network

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AmanogawaApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: AmanogawaApiImpl

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }

        val testService = Ktorfit.Builder()
            .baseUrl(mockWebServer.url("/").toString())
            .httpClient(client)
            .build()
            .createAmanogawaService()

        api = AmanogawaApiImpl(service = testService)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    private fun enqueueResponse(body: String, code: Int = 200) {
        mockWebServer.enqueue(
            MockResponse()
                .setBody(body)
                .setResponseCode(code)
                .addHeader("Content-Type", "application/json")
        )
    }

    @Test
    fun `getCurrentTitles should return list of anime when response is successful`() = runTest {
        enqueueResponse("""[{"id": 1, "name": "Anime 1"}, {"id": 2, "name": "Anime 2"}]""")

        val result = api.getCurrentTitles()

        assertTrue(result.isSuccess)
        val titles = result.getOrNull()
        assertEquals(2, titles?.size)
        assertEquals(1L, titles?.get(0)?.animeId)
    }

    @Test
    fun `getTitlesByPage should return data and set currentPage`() = runTest {
        enqueueResponse("""{"pages": 5, "data": [{"id": 123, "name": "Test Anime"}]}""")

        val result = api.getTitlesByPage(2)

        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals(2, data?.currentPage)
        assertEquals(5, data?.pages)
    }

    @Test
    fun `getTitlesByPage should return failure when page is invalid`() = runTest {
        val result = api.getTitlesByPage(0)
        assertTrue(result.isFailure)
        assertEquals("Page:0 is not exist", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getAll should fetch all pages and combine data`() = runTest {
        // Page 1
        enqueueResponse("""{"pages": 2, "data": [{"id": 1, "name": "A1"}]}""")
        // Page 2
        enqueueResponse("""{"pages": 2, "data": [{"id": 2, "name": "A2"}]}""")

        val result = api.getAll()

        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals(2, data?.data?.size)
        assertEquals(1L, data?.data?.get(0)?.animeId)
        assertEquals(2L, data?.data?.get(1)?.animeId)
    }

    @Test
    fun `getAnime should return anime details`() = runTest {
        enqueueResponse(
            """{
            "id": 789, 
            "name": "Detailed Anime", 
            "en_jp_name": "EN Name",
            "year": 2024,
            "company": "MAPPA",
            "director": "Director",
            "descrition": "Desc",
            "bot_id": 1,
            "season": 1,
            "part": 1,
            "views": 100,
            "dubed_episodes": 12,
            "extra_total": 0,
            "ova_total": 0,
            "is_current": true,
            "poster_thumb": "thumb",
            "poster_mini": "mini",
            "episode_count": "12",
            "bot_url": "url"
        }"""
        )

        val result = api.getAnime(789)

        assertTrue(result.isSuccess)
        assertEquals(789L, result.getOrNull()?.animeId)
    }

    @Test
    fun `getAnimeEpisodes should fetch all pages of episodes`() = runTest {
        // Page 1
        enqueueResponse(
            """{
            "pages": 2, 
            "bot_url": "url", 
            "data": [{
                "id": 10, 
                "title_id": 123,
                "bot_id": 1,
                "name": "Ep 1",
                "screen": "s",
                "screen_original": "so"
            }]
        }"""
        )
        // Page 2
        enqueueResponse(
            """{
            "pages": 2, 
            "bot_url": "url", 
            "data": [{
                "id": 11, 
                "title_id": 123,
                "bot_id": 1,
                "name": "Ep 2",
                "screen": "s",
                "screen_original": "so"
            }]
        }"""
        )

        val result = api.getAnimeEpisodes(123)

        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals(2, data?.data?.size)
        assertEquals("url", data?.botUrl)
    }

    @Test
    fun `getFilter should return filter options`() = runTest {
        enqueueResponse("""{"genres": [{"id": 1, "name": "Action"}], "years": [2023]}""")

        val result = api.getFilter()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.genres?.size)
    }

    @Test
    fun `getFilteredTitles should call service with correct params`() = runTest {
        enqueueResponse("""{"pages": 1, "data": []}""")

        val result = api.getFilteredTitles(
            page = 1,
            years = listOf(2023),
            genres = listOf(1L),
            voices = null,
            sounds = null,
            translators = null
        )

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("y=%5B2023%5D"))
        assertTrue(request.path!!.contains("g=%5B1%5D"))
    }

    @Test
    fun `getRandomAnime should return anime ID`() = runTest {
        enqueueResponse("""[" 456 "]""")

        val result = api.getRandomAnime()

        assertTrue(result.isSuccess)
        assertEquals(456L, result.getOrNull())
    }

    @Test
    fun `searchAnimeByName should return search results`() = runTest {
        enqueueResponse(
            """[{
            "id": 1, 
            "name": "Result",
            "en_jp_name": "EN",
            "year": 2024,
            "season": 1,
            "part": 1,
            "poster_mini": "mini"
        }]"""
        )

        val result = api.searchAnimeByName("query")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)

        val request = mockWebServer.takeRequest()
        assertTrue(request.path!!.contains("s=%22query%22"))
    }

    @Test
    fun `getTeam should return team members`() = runTest {
        enqueueResponse("""[{"id": 1, "name": "Member"}]""")

        val result = api.getTeam()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }
}
