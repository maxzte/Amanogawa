package id.anison.amanogawa.core.database

import id.anison.amanogawa.core.database.favorite.FavoriteDao
import id.anison.amanogawa.core.database.favorite.FavoriteDbo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FavoriteDaoTest : DatabaseTest() {

    private lateinit var dao: FavoriteDao

    private val fav1 = FavoriteDbo(
        favoriteId = 1, isFavorite = true, isCurrent = true, name = "A",
        enJpName = "A", season = 1, part = 1, year = 2024, poster = "p", posterThumb = "pt",
        views = 100, liked = 10, disliked = 2, timestamp = 100L,
    )
    private val fav2 = fav1.copy(favoriteId = 2, name = "B", timestamp = 200L)

    @Before
    override fun setUp() {
        super.setUp()
        dao = db.favoriteDao()
    }

    @Test
    fun `insert and getAll returns items ordered by timestamp desc`() = runTest {
        dao.insertFavorite(fav1)
        dao.insertFavorite(fav2)
        val all = dao.getAll().first()
        assertEquals(2, all.size)
        assertEquals("B", all.first().name)
    }

    @Test
    fun `getAll returns empty flow initially`() = runTest {
        val all = dao.getAll().first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun `get by id returns correct item`() = runTest {
        dao.insertFavorite(fav1)
        dao.insertFavorite(fav2)
        val result = dao.getFavoriteById(1)
        assertEquals(fav1, result)
    }

    @Test
    fun `get by id returns null when missing`() = runTest {
        assertNull(dao.getFavoriteById(999))
    }

    @Test
    fun `isFavorite returns true for existing`() = runTest {
        dao.insertFavorite(fav1)
        val result = dao.isFavorite(1).first()
        assertTrue(result)
    }

    @Test
    fun `isFavorite returns false for missing`() = runTest {
        val result = dao.isFavorite(999).first()
        assertFalse(result)
    }

    @Test
    fun `delete removes specific item`() = runTest {
        dao.insertFavorite(fav1)
        dao.insertFavorite(fav2)
        dao.deleteFavorite(fav1)
        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals(2, all.first().favoriteId)
    }

    @Test
    fun `deleteAllFavorites removes everything`() = runTest {
        dao.insertFavorite(fav1)
        dao.insertFavorite(fav2)
        dao.deleteAllFavorites()
        val all = dao.getAll().first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun `insert replaces existing id`() = runTest {
        dao.insertFavorite(fav1)
        val updated = fav1.copy(views = 999)
        dao.insertFavorite(updated)
        val result = dao.getFavoriteById(1)
        assertEquals(999, result?.views)
    }
}
