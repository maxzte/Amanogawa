package id.anison.amanogawa.core.database

import androidx.paging.PagingSource
import id.anison.amanogawa.core.database.anime.AnimeDao
import id.anison.amanogawa.core.database.anime.entity.AnimeDbo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AnimeDaoTest : DatabaseTest() {

    private lateinit var dao: AnimeDao

    private val anime1 = AnimeDbo(
        animeId = 1, name = "A", isCurrent = true, enJpName = "A", dubbedEpisodes = 0,
        episodesCustom = null, episodesTotal = "12", extraTotal = 0, ovaTotal = 0,
        year = 2024, season = 1, part = 1, views = 100,
        posterThumb = "t1", posterMini = "m1", schedule = "", episodeCount = "12",
    )
    private val anime2 = anime1.copy(animeId = 2, name = "B", views = 50)

    @Before
    override fun setUp() {
        super.setUp()
        dao = db.animeDao()
    }

    @Test
    fun `insert and get by id`() = runTest {
        dao.insertAnime(anime1)
        val result = dao.getAnimeById(1)
        assertEquals(anime1, result)
    }

    @Test
    fun `get by id returns null for missing`() = runTest {
        assertNull(dao.getAnimeById(999))
    }

    @Test
    fun `insert list and getAll`() = runTest {
        dao.insert(listOf(anime1, anime2))
        val all = dao.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `insert replaces existing`() = runTest {
        dao.insertAnime(anime1)
        val updated = anime1.copy(views = 200)
        dao.insertAnime(updated)
        val result = dao.getAnimeById(1)
        assertEquals(200, result?.views)
    }

    @Test
    fun `remove deletes items`() = runTest {
        dao.insert(listOf(anime1, anime2))
        dao.remove(listOf(anime1))
        assertNull(dao.getAnimeById(1))
        assertNotNull(dao.getAnimeById(2))
    }

    @Test
    fun `clearAll removes everything`() = runTest {
        dao.insert(listOf(anime1, anime2))
        dao.clearAll()
        assertTrue(dao.getAll().isEmpty())
    }

    @Test
    fun `clearAllByListKey removes only matching`() = runTest {
        val a1 = anime1.copy(listKey = "key_a")
        val a2 = anime2.copy(listKey = "key_b")
        dao.insert(listOf(a1, a2))
        dao.clearAllByListKey("key_a")
        assertNull(dao.getAnimeById(1))
        assertNotNull(dao.getAnimeById(2))
    }

    @Test
    fun `observeAll emits inserted data`() = runTest {
        dao.insertAnime(anime1)
        val result = dao.observeAll().first()
        assertEquals(1, result.size)
        assertEquals(anime1, result.first())
    }

    @Test
    fun `pagingAll returns items ordered by views desc`() = runTest {
        dao.insert(listOf(anime1.copy(views = 10), anime2.copy(views = 99)))
        val result = dao.pagingAll().load(
            PagingSource.LoadParams.Refresh(null, 20, false)
        )
        val page = result as PagingSource.LoadResult.Page
        assertEquals(2, page.data.size)
        assertEquals(99, page.data[0].views)
    }
}
