package id.anison.amanogawa.core.database

import id.anison.amanogawa.core.database.anime.CurrentAnimeDao
import id.anison.amanogawa.core.database.anime.entity.CurrentAnimeDbo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CurrentAnimeDaoTest : DatabaseTest() {

    private lateinit var dao: CurrentAnimeDao

    private val item1 = CurrentAnimeDbo(
        currentAnimeId = 1, name = "A", enJpName = "A",
        episodesCustom = null, episodesTotal = "12", extraTotal = 0, ovaTotal = 0,
        episodeCount = "12", newEpisode = 5, schedule = null, time = 0.0, timeStr = "",
        isCurrent = true,
    )
    private val item2 = item1.copy(currentAnimeId = 2, name = "B")

    @Before
    override fun setUp() {
        super.setUp()
        dao = db.currentAnimeDao()
    }

    @Test
    fun `insert list and getAll`() = runTest {
        dao.insertCurrentAnimeList(listOf(item1, item2))
        val all = dao.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun `getAll returns empty initially`() = runTest {
        assertTrue(dao.getAll().isEmpty())
    }

    @Test
    fun `insert replaces existing ids`() = runTest {
        dao.insertCurrentAnimeList(listOf(item1))
        val updated = item1.copy(newEpisode = 10)
        dao.insertCurrentAnimeList(listOf(updated))
        val all = dao.getAll()
        assertEquals(1, all.size)
        assertEquals(10, all.first().newEpisode)
    }

    @Test
    fun `delete selected items`() = runTest {
        dao.insertCurrentAnimeList(listOf(item1, item2))
        dao.deleteNotCurrentAnimeList(listOf(item1))
        val all = dao.getAll()
        assertEquals(1, all.size)
        assertEquals(2, all.first().currentAnimeId)
    }
}
