package id.anison.amanogawa.core.database

import id.anison.amanogawa.core.database.search.SearchResultDao
import id.anison.amanogawa.core.database.search.SearchResultDbo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchResultDaoTest : DatabaseTest() {

    private lateinit var dao: SearchResultDao

    private val result1 = SearchResultDbo(
        animeId = 1, name = "Anime1", enJpName = "A1",
        year = 2024, season = 1, part = 1, posterMini = "p1", timestamp = 100L,
    )
    private val result2 = result1.copy(animeId = 2, name = "Anime2", timestamp = 200L)

    @Before
    override fun setUp() {
        super.setUp()
        dao = db.searchResultDao()
    }

    @Test
    fun `insert and getAll returns items ordered by timestamp desc`() = runTest {
        dao.insertSearchResult(result1)
        dao.insertSearchResult(result2)
        val all = dao.getAll().first()
        assertEquals(2, all.size)
        assertEquals("Anime2", all.first().name)
    }

    @Test
    fun `getAll returns empty flow initially`() = runTest {
        val all = dao.getAll().first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun `delete removes specific result`() = runTest {
        dao.insertSearchResult(result1)
        dao.insertSearchResult(result2)
        dao.deleteSearchResult(result1)
        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals(2, all.first().animeId)
    }

    @Test
    fun `clearSearchHistory removes all`() = runTest {
        dao.insertSearchResult(result1)
        dao.insertSearchResult(result2)
        dao.clearSearchHistory()
        val all = dao.getAll().first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun `insert replaces existing id`() = runTest {
        dao.insertSearchResult(result1)
        val updated = result1.copy(name = "Updated")
        dao.insertSearchResult(updated)
        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals("Updated", all.first().name)
    }
}
