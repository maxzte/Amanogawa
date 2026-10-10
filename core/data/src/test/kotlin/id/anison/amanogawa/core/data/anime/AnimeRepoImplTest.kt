package id.anison.amanogawa.core.data.anime

import androidx.room.withTransaction
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.database.anime.AnimeDao
import id.anison.amanogawa.core.database.anime.CurrentAnimeDao
import id.anison.amanogawa.core.database.anime.entity.CurrentAnimeDbo
import id.anison.amanogawa.core.network.AmanogawaApi
import id.anison.amanogawa.core.network.model.anime.AnimeDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AnimeRepoImplTest {

    private val database: AmanogawaDatabase = mockk(relaxed = true)
    private val api: AmanogawaApi = mockk()
    private val animeDao: AnimeDao = mockk(relaxed = true)
    private val currentAnimeDao: CurrentAnimeDao = mockk(relaxed = true)

    private lateinit var repository: AnimeRepoImpl

    @BeforeEach
    fun setUp() {
        mockkStatic("androidx.room.RoomDatabaseKt")
        every { database.animeDao() } returns animeDao
        every { database.currentAnimeDao() } returns currentAnimeDao

        val lambdaSlot = slot<suspend () -> Any>()
        coEvery { database.withTransaction(capture(lambdaSlot)) } coAnswers {
            lambdaSlot.captured.invoke()
        }

        repository = AnimeRepoImpl(database, api)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAnimeListWithNewEpisodes when local DB is empty populates DB and returns empty list`() = runTest {
        val remoteDto = createAnimeDto(id = 1, newEpisode = 10)
        coEvery { api.getCurrentTitles() } returns Result.success(listOf(remoteDto))
        coEvery { currentAnimeDao.getAll() } returns emptyList()

        val result = repository.getAnimeListWithNewEpisodes()

        assertTrue(result is DataState.Success)
        val data = (result as DataState.Success).data
        assertTrue(data.isEmpty())

        coVerify(exactly = 1) {
            currentAnimeDao.insertCurrentAnimeList(any())
        }
    }

    @Test
    fun `getAnimeListWithNewEpisodes when no new episodes returns empty list`() = runTest {
        val remoteDto = createAnimeDto(id = 1, newEpisode = 5)
        val localDbo = createCurrentAnimeDbo(id = 1, newEpisode = 5)

        coEvery { api.getCurrentTitles() } returns Result.success(listOf(remoteDto))
        coEvery { currentAnimeDao.getAll() } returns listOf(localDbo)

        val result = repository.getAnimeListWithNewEpisodes()

        assertTrue(result is DataState.Success)
        val data = (result as DataState.Success).data
        assertTrue(data.isEmpty())

        coVerify(exactly = 1) {
            currentAnimeDao.insertCurrentAnimeList(any())
            currentAnimeDao.deleteNotCurrentAnimeByIds(listOf(1L))
        }
    }

    @Test
    fun `getAnimeListWithNewEpisodes when new episode is available returns filtered anime`() = runTest {
        val remote1 = createAnimeDto(id = 1, name = "Anime 1", newEpisode = 6)
        val remote2 = createAnimeDto(id = 2, name = "Anime 2", newEpisode = 3)

        val local1 = createCurrentAnimeDbo(id = 1, newEpisode = 5)
        val local2 = createCurrentAnimeDbo(id = 2, newEpisode = 3)

        coEvery { api.getCurrentTitles() } returns Result.success(listOf(remote1, remote2))
        coEvery { currentAnimeDao.getAll() } returns listOf(local1, local2)

        val result = repository.getAnimeListWithNewEpisodes()

        assertTrue(result is DataState.Success)
        val data = (result as DataState.Success).data
        assertEquals(1, data.size)
        assertEquals(1L, data.first().animeId)
        assertEquals(6, data.first().newEpisode)

        coVerify(exactly = 1) {
            currentAnimeDao.insertCurrentAnimeList(any())
            currentAnimeDao.deleteNotCurrentAnimeByIds(listOf(1L, 2L))
        }
    }

    @Test
    fun `getAnimeListWithNewEpisodes when anime removed from remote deletes obsolete from DB`() = runTest {
        val remote1 = createAnimeDto(id = 1, newEpisode = 5)
        val local1 = createCurrentAnimeDbo(id = 1, newEpisode = 5)
        val local2 = createCurrentAnimeDbo(id = 2, newEpisode = 10)

        coEvery { api.getCurrentTitles() } returns Result.success(listOf(remote1))
        coEvery { currentAnimeDao.getAll() } returns listOf(local1, local2)

        val result = repository.getAnimeListWithNewEpisodes()

        assertTrue(result is DataState.Success)
        val data = (result as DataState.Success).data
        assertTrue(data.isEmpty())

        coVerify(exactly = 1) {
            currentAnimeDao.deleteNotCurrentAnimeByIds(listOf(1L))
        }
    }

    @Test
    fun `getAnimeListWithNewEpisodes when API fails returns error`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { api.getCurrentTitles() } returns Result.failure(exception)

        val result = repository.getAnimeListWithNewEpisodes()

        assertTrue(result is DataState.Error)
        assertEquals("Network error", (result as DataState.Error).exception?.message)

        coVerify(exactly = 0) {
            currentAnimeDao.insertCurrentAnimeList(any())
        }
    }

    private fun createAnimeDto(
        id: Long,
        name: String = "Test Anime",
        newEpisode: Int = 1,
    ) = AnimeDto(
        animeId = id,
        name = name,
        isCurrent = true,
        enJpName = "Test Anime En",
        dubedEpisodes = 0,
        episodesCustom = "12",
        episodesTotal = "12",
        extraTotal = 0,
        ovaTotal = 0,
        posterThumb = "thumb",
        posterMini = "mini",
        schedule = "Monday",
        episodeCount = "12",
        newEpisode = newEpisode,
        timeStr = "12:00"
    )

    private fun createCurrentAnimeDbo(
        id: Long,
        name: String = "Test Anime",
        newEpisode: Int = 1,
    ) = CurrentAnimeDbo(
        currentAnimeId = id,
        name = name,
        isCurrent = true,
        enJpName = "Test Anime En",
        episodesCustom = "12",
        episodesTotal = "12",
        extraTotal = 0,
        ovaTotal = 0,
        schedule = "Monday",
        episodeCount = "12",
        newEpisode = newEpisode,
        time = 0.0,
        timeStr = "12:00"
    )
}
