package id.anison.amanogawa.core.domain

import id.anison.amanogawa.core.domain.model.Anime
import id.anison.amanogawa.core.model.AnimeUI

fun Anime.toAnimeUI() = AnimeUI(
    animeId = animeId,
    name = name,
    isCurrent = isCurrent,
    enJpName = enJpName,
    dubbedEpisodes = dubbedEpisodes,
    episodesCustom = episodesCustom,
    episodesTotal = episodesTotal,
    extraTotal = if (extraTotal != 0) season.toString() else "",
    ovaTotal = if (ovaTotal != 0) season.toString() else "",
    year = year.toString(),
    season = if (season != 0.toByte()) season.toString() else "",
    part = if (part != 0.toByte()) part.toString() else "",
    views = views.toString(),
    posterThumb = posterThumb,
    posterMini = posterMini,
    schedule = schedule,
    episodeCount = episodeCount,
    newEpisode = newEpisode,
    isExtra = isExtra,
    isOva = isOva,
    time = time,
    timeStr = timeStr,
)
