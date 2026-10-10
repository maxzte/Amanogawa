package id.anison.amanogawa.core.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import id.anison.amanogawa.core.domain.model.Anime
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NewEpisodesNotifierTest {

    private val context: Context = mockk(relaxed = true)
    private val packageManager: PackageManager = mockk(relaxed = true)
    private val notificationManagerCompat: NotificationManagerCompat = mockk(relaxed = true)
    private val pendingIntent: PendingIntent = mockk(relaxed = true)
    private val mockNotification: Notification = mockk(relaxed = true)

    private lateinit var notifier: NewEpisodesNotifier

    @BeforeEach
    fun setUp() {
        mockkStatic(NotificationManagerCompat::class)
        mockkStatic(PendingIntent::class)
        mockkConstructor(NotificationCompat.Builder::class)

        every { NotificationManagerCompat.from(any()) } returns notificationManagerCompat
        every { PendingIntent.getActivity(any(), any(), any(), any()) } returns pendingIntent
        every { anyConstructed<NotificationCompat.Builder>().build() } returns mockNotification

        every { context.packageName } returns "id.anison.amanogawa"
        every { context.packageManager } returns packageManager
        every { packageManager.getLaunchIntentForPackage("id.anison.amanogawa") } returns null
        every { context.getString(any()) } returns "Notification Text"
        every { context.getString(any(), *anyVararg()) } returns "Formatted Notification Text"

        notifier = NewEpisodesNotifier(context)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `postNewEpisodes with single anime posts individual notification without summary`() {
        val anime = createAnime(id = 101L, name = "Solo Leveling")

        notifier.postNewEpisodes(listOf(anime))

        verify(exactly = 1) { notificationManagerCompat.notify(101, mockNotification) }
        verify(exactly = 0) { notificationManagerCompat.notify(0, any()) }
    }

    @Test
    fun `postNewEpisodes with multiple anime posts individual notifications and group summary`() {
        val anime1 = createAnime(id = 201L, name = "Anime One")
        val anime2 = createAnime(id = 202L, name = "Anime Two")

        notifier.postNewEpisodes(listOf(anime1, anime2))

        verify(exactly = 1) { notificationManagerCompat.notify(201, mockNotification) }
        verify(exactly = 1) { notificationManagerCompat.notify(202, mockNotification) }
        verify(exactly = 1) { notificationManagerCompat.notify(0, mockNotification) }
    }

    @Test
    fun `postNewEpisodes with empty list posts no notifications`() {
        notifier.postNewEpisodes(emptyList())

        verify(exactly = 0) { notificationManagerCompat.notify(any(), any()) }
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
