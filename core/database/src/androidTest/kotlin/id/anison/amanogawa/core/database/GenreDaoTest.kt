package id.anison.amanogawa.core.database

import id.anison.amanogawa.core.database.genre.GenreDao
import id.anison.amanogawa.core.database.genre.GenreDbo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GenreDaoTest : DatabaseTest() {

    private lateinit var dao: GenreDao

    @Before
    override fun setUp() {
        super.setUp()
        dao = db.genreDao()
    }

    @Test
    fun `insert genres and get by name`() = runTest {
        val genre = GenreDbo(genreId = 1, name = "Action", botIdName = "action")
        dao.insertGenres(listOf(genre))
        val result = dao.getGenreByGenreName("Action")
        assertEquals(genre, result)
    }

    @Test
    fun `get by name returns null when not found`() = runTest {
        assertNull(dao.getGenreByGenreName("nonexistent"))
    }

    @Test
    fun `insert multiple genres and get by name`() = runTest {
        dao.insertGenres(
            listOf(
                GenreDbo(genreId = 1, name = "Action"),
                GenreDbo(genreId = 2, name = "Comedy"),
            )
        )
        val result = dao.getGenreByGenreName("Comedy")
        assertEquals(2L, result?.genreId)
    }

    @Test
    fun `insert replaces existing genre`() = runTest {
        val genre = GenreDbo(genreId = 1, name = "Action", botIdName = "old")
        dao.insertGenres(listOf(genre))
        val updated = GenreDbo(genreId = 1, name = "Action", botIdName = "new")
        dao.insertGenres(listOf(updated))
        val result = dao.getGenreByGenreName("Action")
        assertEquals("new", result?.botIdName)
    }
}
