package id.anison.amanogawa.core.database

import id.anison.amanogawa.core.database.anime.AnimeDetailsDao
import id.anison.amanogawa.core.database.anime.entity.AnimeDetailsDbo
import id.anison.amanogawa.core.database.genre.GenreDbo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AnimeDetailsDaoTest : DatabaseTest() {

    private lateinit var dao: AnimeDetailsDao

    private val genre = GenreDbo(genreId = 1, name = "Action")
    private val details = AnimeDetailsDbo(
        animeId = 1, duration = "24m", company = "X", director = "Y",
        description = "Test", botId = 0, liked = 10, disliked = 2,
        isMovie = false, isDubbed = false, isHaveSubs = true,
        torrentUrl = null, torrent4kUrl = null, posterMaker = "", poster = "",
        botUrl = "url", screensF = emptyList(), genres = listOf(genre), groupF = null,
    )

    @Before
    override fun setUp() {
        super.setUp()
        dao = db.animeDetailsDao()
    }

    @Test
    fun `save and get by id`() = runTest {
        dao.saveAnimeDetails(details)
        val result = dao.getAnimeDetailsById(1)
        assertEquals(details, result)
    }

    @Test
    fun `get by id returns null for missing`() = runTest {
        assertNull(dao.getAnimeDetailsById(999))
    }

    @Test
    fun `delete removes details`() = runTest {
        dao.saveAnimeDetails(details)
        dao.deleteAnimeDetails(details)
        assertNull(dao.getAnimeDetailsById(1))
    }

    @Test
    fun `save replaces existing`() = runTest {
        dao.saveAnimeDetails(details)
        val updated = details.copy(company = "Y2")
        dao.saveAnimeDetails(updated)
        val result = dao.getAnimeDetailsById(1)
        assertEquals("Y2", result?.company)
    }
}
