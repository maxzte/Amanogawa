package id.anison.amanogawa.core.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.domain.model.Anime
import id.anison.amanogawa.core.domain.repository.AnimeRepo
import id.anison.amanogawa.core.notification.Notifier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NotificationWorkerTest {

    private val context: Context = mockk(relaxed = true)
    private val workerParams: WorkerParameters = mockk(relaxed = true)
    private val animeRepo: AnimeRepo = mockk()
    private val notifier: Notifier = mockk(relaxed = true)

    private lateinit var worker: NotificationWorker

    @BeforeEach
    fun setUp() {
        worker = NotificationWorker(
            context = context,
            workerParams = workerParams,
            animeRepo = animeRepo,
            notifier = notifier
        )
    }

    @Test
    fun `doWork when new episodes available posts notifications and returns success`() = runTest {
        val animeList = listOf(createAnime(id = 1L, name = "Test Anime"))
        coEvery { animeRepo.getAnimeListWithNewEpisodes() } returns DataState.Success(animeList)

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 1) { notifier.postNewEpisodes(animeList) }
    }

    @Test
    fun `doWork when no new episodes does not post notifications and returns success`() = runTest {
        coEvery { animeRepo.getAnimeListWithNewEpisodes() } returns DataState.Success(emptyList())

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 0) { notifier.postNewEpisodes(any()) }
    }

    @Test
    fun `doWork when repo returns error returns retry`() = runTest {
        coEvery { animeRepo.getAnimeListWithNewEpisodes() } returns DataState.Error(RuntimeException("Error"))

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
        coVerify(exactly = 0) { notifier.postNewEpisodes(any()) }
    }

    @Test
    fun `doWork when repo throws exception returns failure`() = runTest {
        coEvery { animeRepo.getAnimeListWithNewEpisodes() } throws RuntimeException("Fatal error")

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.failure(), result)
        coVerify(exactly = 0) { notifier.postNewEpisodes(any()) }
    }

    private fun createAnime(
        id: Long,
        name: String,
    ) = Anime(
        animeId = id,
        name = name,
        isCurrent = true,
        enJpName = "Test",
        dubbedEpisodes = 0,
        episodesCustom = "12",
        episodesTotal = "12",
        extraTotal = 0,
        ovaTotal = 0,
        year = 2024.toShort(),
        season = 1.toByte(),
        part = 1.toByte(),
        views = 100,
        posterThumb = "thumb",
        posterMini = "mini",
        schedule = "Monday",
        episodeCount = "12",
        newEpisode = 1,
        isExtra = false,
        isOva = false,
        time = 0.0,
        timeStr = "12:00"
    )
}
