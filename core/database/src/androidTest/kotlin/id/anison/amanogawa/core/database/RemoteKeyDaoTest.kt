package id.anison.amanogawa.core.database

import id.anison.amanogawa.core.database.anime.RemoteKeyDao
import id.anison.amanogawa.core.database.anime.entity.RemoteKeyDbo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemoteKeyDaoTest : DatabaseTest() {

    private lateinit var dao: RemoteKeyDao

    private val key1 = RemoteKeyDbo(key = "default", currentPage = 1, nextPage = 2, lastUpdated = 1000L)
    private val key2 = RemoteKeyDbo(key = "filtered", currentPage = 3, nextPage = null, lastUpdated = 2000L)

    @Before
    override fun setUp() {
        super.setUp()
        dao = db.remoteKeyDao()
    }

    @Test
    fun `insert and get single key`() = runTest {
        dao.insertKey(key1)
        val result = dao.getKey("default")
        assertEquals(key1, result)
    }

    @Test
    fun `get key returns null for missing`() = runTest {
        assertNull(dao.getKey("nonexistent"))
    }

    @Test
    fun `insert keys and get expired`() = runTest {
        dao.insertKeys(listOf(key1, key2))
        val expired = dao.getExpiredKeys(1500L)
        assertEquals(1, expired.size)
        assertEquals("filtered", expired.first().key)
    }

    @Test
    fun `insert keys and getExpired returns none when threshold is past`() = runTest {
        dao.insertKeys(listOf(key1, key2))
        val expired = dao.getExpiredKeys(3000L)
        assertTrue(expired.isEmpty())
    }

    @Test
    fun `delete key removes it`() = runTest {
        dao.insertKey(key1)
        dao.deleteKey("default")
        assertNull(dao.getKey("default"))
    }

    @Test
    fun `clearKeys removes all`() = runTest {
        dao.insertKeys(listOf(key1, key2))
        dao.clearKeys()
        assertNull(dao.getKey("default"))
        assertNull(dao.getKey("filtered"))
    }

    @Test
    fun `insert replaces existing key`() = runTest {
        dao.insertKey(key1)
        val updated = key1.copy(currentPage = 5)
        dao.insertKey(updated)
        assertEquals(5, dao.getKey("default")?.currentPage)
    }
}
