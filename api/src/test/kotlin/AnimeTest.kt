import id.anison.api.AmanogawaApi
import id.anison.api.AmanogawaApiImpl
import id.anison.api.model.Genre
import id.anison.api.model.anime.Anime
import id.anison.api.model.anime.AnimeDetails
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class AnimeTest {
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
    fun `getAllAnime should return list`() = runTest {
        val res = api.getAll()
        assertTrue(res.isSuccess)
    }

    @Test
    fun `getAnime should return valid Anime object on success`() = runTest {
        val expectedAnimeDetails = AnimeDetails(
            animeId = 115,
            name = "Тільки я візьму новий рівень",
            enJpName = "Ore dake Level Up na Ken",
            year = 2024,
            duration = "~ 23 хв.",
            company = "A-1 Pictures",
            director = "Накашіґе Шунсуке",
            description = "На Землі почали відкриватися портали з яких полізли монстри. Та разом з цим багато людей відкрили в собі магічні сили та почали використовувати їх для винищення цих монстрів й закриття порталів. Їх називають мисливцями.\r\nСонг Джінху також отримав магічні сили, та вони в нього настільки малі, що він лиш трохи сильніший за звичайну людину. Навіть найслабші монстри – складні суперники для нього. На жаль, ніхто з мисливців не може збільшити свою силу. Виходить, Сонг так і буде найслабшим мисливцем?\r\n\r\nМожливо, так би й сталося, та на одному з завдань він разом з групою інших мисливців знаходять храм древнього бога...",
            botId = 94,
            season = 0,
            part = 0,
            views = 19098,
            postDate = "6 СІЧНЯ, 2024",
            postUpdate = "14 ЛЮТИЙ, 2025",
            episodesTotal = "12",
            episodesCustom = null,
            dubedEpisodes = 0,
            extraTotal = 0,
            ovaTotal = 0,
            liked = 3444,
            disliked = 67,
            isCurrent = false,
            isMovie = false,
            isDubed = false,
            isHaveSubs = true,
            torrentUrl = "https://toloka.to/t675810",
            torrent4kUrl = null,
            posterMaker = "shelkin",
            poster = "/media/images/posters/2024/poster_EPnZXkt.jpg",
            posterThumb = "/media/images/posters/2024/posters_thumbs/poster_EPnZXkt.jpg",
            posterMini = "/media/images/posters/2024/posters_mini/poster_EPnZXkt.jpg",
            screensF = listOf(
                listOf(
                    "/media/images/titles_screens/2024/title_screen_cEMiDgo.jpg",
                    "/media/images/titles_screens/2024/thumbs/title_screen_cEMiDgo.jpg"
                ),
                listOf(
                    "/media/images/titles_screens/2024/title_screen_GV73f4M.jpg",
                    "/media/images/titles_screens/2024/thumbs/title_screen_GV73f4M.jpg"
                )
            ),
            genres = listOf(
                Genre(11, "бойовик"),
                Genre(23, "пригоди"),
                Genre(12, "фентезі")
            ),
            soundsF = listOf(listOf("Rainy984")),
            /*voicesF = listOf(
                listOf(
                    9L to "Alisand",
                    1L to "Bodya500icq",
                    40L to "DedrDs",
                    2L to "Rainy984",
                    3L to "Spalah",
                    8L to "Suni",
                    13L to "Yunko"
                )
            ),*/
            translF = listOf(listOf("Furunze")),
            /*dubers = Dubers1(
                s = listOf(1L to "Rainy984"),
                v = listOf(
                    9L to "Alisand",
                    1L to "Bodya500icq",
                    40L to "DedrDs",
                    2L to "Rainy984",
                    3L to "Spalah",
                    8L to "Suni",
                    13L to "Yunko"
                ),
                t = listOf(1L to "Furunze")
            ),*/
            /*dubers = Dubers(
                s = mapOf(1L to "Rainy984"),
                v = mapOf(
                    9L to "Alisand",
                    1L to "Bodya500icq",
                    40L to "DedrDs",
                    2L to "Rainy984",
                    3L to "Spalah",
                    8L to "Suni",
                    13L to "Yunko",
                ),
                t = mapOf(1L to "Furunze"),
            ),*/
            groupF = listOf(
                Anime(
                    animeId = 138,
                    name = "Тільки я візьму новий рівень",
                    isCurrent = true,
                    enJpName = "Ore dake Level Up na Ken Season 2: Arise from the Shadow",
                    dubedEpisodes = 0,
                    episodesCustom = null,
                    episodesTotal = 13.toString(),
                    extraTotal = 0,
                    ovaTotal = 0,
                    year = 2025,
                    season = 2,
                    part = 0,
                    views = 14156,
                    posterThumb = "/media/images/posters/2025/posters_thumbs/poster.jpg",
                    posterMini = "/media/images/posters/2025/posters_mini/poster.jpg",
                    schedule = "~ субота 23:00 — неділя 4:00",
                )
            ),
            episodeCount = "12",
            botUrl = "https://t.me/amanogawa_ua_bot"
        )

        val result = api.getCurrentTitles(/*115L*/)

        assertTrue(result.isSuccess)
//        assertEquals(115L, result.getOrNull()?.id)
//        assertEquals(expectedAnimeDetails.genres, result.getOrNull()?.genres)
    }
}