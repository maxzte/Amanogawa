package id.anison.amanogawa.core.database

import id.anison.amanogawa.core.database.filter.FilterDao
import id.anison.amanogawa.core.database.filter.FilterDbo
import id.anison.amanogawa.core.database.filter.FilterGenreCrossRef
import id.anison.amanogawa.core.database.filter.FilterTypeDbo
import id.anison.amanogawa.core.database.filter.FilterYearDbo
import id.anison.amanogawa.core.database.genre.GenreDbo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FilterDaoTest : DatabaseTest() {

    private lateinit var dao: FilterDao

    @Before
    override fun setUp() {
        super.setUp()
        dao = db.filterDao()
    }

    @Test
    fun `insert and get filter`() = runTest {
        val filter = FilterDbo(FilterTypeDbo.DISPLAYED)
        dao.insertFilter(filter)
        val result = dao.getFilter(FilterTypeDbo.DISPLAYED)
        assertEquals(filter, result)
    }

    @Test
    fun `get filter returns null for missing type`() = runTest {
        assertNull(dao.getFilter(FilterTypeDbo.SEARCH))
    }

    @Test
    fun `insert years and get with years`() = runTest {
        val filter = FilterDbo(FilterTypeDbo.DISPLAYED)
        dao.insertFilter(filter)
        val years = listOf(
            FilterYearDbo(year = 2024, filterType = FilterTypeDbo.DISPLAYED),
            FilterYearDbo(year = 2025, filterType = FilterTypeDbo.DISPLAYED),
        )
        dao.insertYears(years)
        val result = dao.getFilterWithYears(FilterTypeDbo.DISPLAYED)
        assertEquals(2, result.years.size)
    }

    @Test
    fun `delete years removes them`() = runTest {
        val filter = FilterDbo(FilterTypeDbo.DISPLAYED)
        dao.insertFilter(filter)
        dao.insertYears(listOf(FilterYearDbo(year = 2024, filterType = FilterTypeDbo.DISPLAYED)))
        dao.deleteYears(FilterTypeDbo.DISPLAYED)
        val result = dao.getFilterWithYears(FilterTypeDbo.DISPLAYED)
        assertNotNull(result)
    }

    @Test
    fun `insert genre refs and get with genres`() = runTest {
        val filter = FilterDbo(FilterTypeDbo.DISPLAYED)
        dao.insertFilter(filter)
        val genre = GenreDbo(genreId = 1, name = "Action")
        db.genreDao().insertGenres(listOf(genre))
        dao.insertGenreRefs(listOf(FilterGenreCrossRef(FilterTypeDbo.DISPLAYED, 1)))
        val result = dao.getFilterWithGenres(FilterTypeDbo.DISPLAYED)
        assertEquals(1, result.genres.size)
        assertEquals("Action", result.genres.first().name)
    }

    @Test
    fun `delete genre refs removes them`() = runTest {
        val filter = FilterDbo(FilterTypeDbo.DISPLAYED)
        dao.insertFilter(filter)
        db.genreDao().insertGenres(listOf(GenreDbo(genreId = 1, name = "Action")))
        dao.insertGenreRefs(listOf(FilterGenreCrossRef(FilterTypeDbo.DISPLAYED, 1)))
        dao.deleteGenreRefs(FilterTypeDbo.DISPLAYED)
        val result = dao.getFilterWithGenres(FilterTypeDbo.DISPLAYED)
        assertNotNull(result)
    }

    @Test
    fun `remove filter deletes it`() = runTest {
        val filter = FilterDbo(FilterTypeDbo.DISPLAYED)
        dao.insertFilter(filter)
        dao.remove(filter)
        assertNull(dao.getFilter(FilterTypeDbo.DISPLAYED))
    }

    @Test
    fun `clear removes all filters`() = runTest {
        dao.insertFilter(FilterDbo(FilterTypeDbo.DISPLAYED))
        dao.insertFilter(FilterDbo(FilterTypeDbo.SEARCH))
        dao.clear()
        assertNull(dao.getFilter(FilterTypeDbo.DISPLAYED))
        assertNull(dao.getFilter(FilterTypeDbo.SEARCH))
    }
}
