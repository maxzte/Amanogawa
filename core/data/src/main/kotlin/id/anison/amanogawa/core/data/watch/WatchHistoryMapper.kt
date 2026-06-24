package id.anison.amanogawa.core.data.watch

import id.anison.amanogawa.core.database.watch.WatchHistoryDbo
import id.anison.amanogawa.core.domain.model.WatchHistory

internal fun WatchHistoryDbo.toWatchHistory() = WatchHistory(
    animeId = animeId,
    animeName = animeName,
    animeEnJpName = animeEnJpName,
    animePoster = animePoster,
    animePosterThumb = animePosterThumb,
    timestamp = timestamp,
    lastEpisodeId = lastEpisodeId,
    lastEpisodeNumber = lastEpisodeNumber,
    lastEpisodeTitle = lastEpisodeTitle,
    lastSeason = lastSeason,
    lastPart = lastPart,
    lastYear = lastYear,
)

internal fun WatchHistory.toWatchHistoryDbo() = WatchHistoryDbo(
    animeId = animeId,
    animeName = animeName,
    animeEnJpName = animeEnJpName,
    animePoster = animePoster,
    animePosterThumb = animePosterThumb,
    timestamp = timestamp,
    lastEpisodeId = lastEpisodeId,
    lastEpisodeNumber = lastEpisodeNumber,
    lastEpisodeTitle = lastEpisodeTitle,
    lastSeason = lastSeason,
    lastPart = lastPart,
    lastYear = lastYear,
)
