import id.anison.api.AmanogawaApi
import id.anison.api.AmanogawaApiImpl
import id.anison.api.model.Dubers
import id.anison.api.model.Filter
import id.anison.api.model.Genre
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FilterRepositoryTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: AmanogawaApi


    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        api = AmanogawaApiImpl()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getFilter should return valid Filter object on success`() = runTest {

        val expectedFilter = Filter(
            years = listOf(
                2025, 2024, 2023, 2022, 2021, 2020, 2019, 2018, 2017, 2016, 2015, 2014, 2013,
                2012, 2011, 2010, 2009, 2008, 2006, 2005, 2004, 1999, 1988, 1979
            ),
            genres = listOf(
                Genre(1, "комедія", "genre_komedija"),
                Genre(2, "магія", "genre_mahija"),
                Genre(3, "екшн", "genre_ekshn"),
                Genre(4, "суперсила", "genre_supersyla"),
                Genre(5, "фантастика", "genre_fantastyka"),
                Genre(6, "драма", "genre_drama"),
                Genre(7, "містика", "genre_mistyka"),
                Genre(8, "шьоджьо", "genre_shodzho"),
                Genre(9, "романтика", "genre_romantyka"),
                Genre(10, "детектив", "genre_detektyv"),
                Genre(11, "бойовик", "genre_bojovyk"),
                Genre(12, "фентезі", "genre_fentezi"),
                Genre(13, "наукова фантастика", "genre_naukovafantastyka"),
                Genre(14, "буденність", "genre_budennist"),
                Genre(15, "школа", "genre_shkola"),
                Genre(16, "шьоджьо-ай", "genre_shodzhoaj"),
                Genre(17, "психологія", "genre_psykholohija"),
                Genre(18, "історичний", "genre_istorychnyj"),
                Genre(19, "самураї", "genre_samurayi"),
                Genre(20, "війна", "genre_vijna"),
                Genre(21, "меха", "genre_mekha"),
                Genre(22, "спорт", "genre_sport"),
                Genre(23, "пригоди", "genre_pryhody"),
                Genre(25, "музичний", "genre_muzychnyj"),
                Genre(26, "шьонен-ай", "genre_shonenaj"),
                Genre(27, "шьонен", "genre_shonen"),
                Genre(28, "сейнен", "genre_sejnen"),
                Genre(29, "надприродне", "genre_nadpryrodnye"),
                Genre(30, "бойові мистецтва", "genre_bojovemystetstvo"),
                Genre(31, "трилер", "genre_tryler"),
                Genre(32, "гарем", "genre_harem"),
                Genre(33, "повсякденність", "genre_povsjakdennist"),
                Genre(34, "кулінарія", "genre_kulinarija"),
                Genre(35, "еччі", "genre_echchi"),
                Genre(36, "психологічний трилер", "genre_psykholohichnyjtryler"),
                Genre(37, "жахи", "genre_zhahy"),
            ),
            dubers = Dubers(
                s = mapOf(
                    1L to "Rainy984",
                    2L to "Bodya500icq",
                ),
                t = mapOf(
                    1L to "Furunze",
                    2L to "Серафікус",
                    3L to "AngelBagel",
                    9L to "Andr",
                ),
                v = mapOf(
                    1L to "Bodya500icq",
                    2L to "Rainy984",
                    3L to "Spalah",
                    5L to "maira",
                    6L to "SoN",
                    7L to "Nutix",
                    8L to "Suni",
                    9L to "Alisand",
                    13L to "Yunko",
                    40L to "DedrDs",
                    41L to "Boni",
                    43L to "Yudgi",
                )
            )
        )

        val jsonResponse = Json.encodeToString(expectedFilter)

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val result = api.getFilter()

        assertTrue(result.isSuccess)
        assertEquals(expectedFilter.genres, result.getOrNull()?.genres?.sortedBy { it.genreId })
//        assertEquals(expectedFilter.dubers, result.getOrNull()?.dubers?
        assertEquals(expectedFilter.years, result.getOrNull()?.years)
    }

    /*@Test
    fun `getFilter should return failure on HTTP error`() = runTest {

        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        val result = api.getFilter()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }*/
}
