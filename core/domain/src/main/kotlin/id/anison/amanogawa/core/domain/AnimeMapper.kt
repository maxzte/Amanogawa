package id.anison.amanogawa.core.domain

import id.anison.amanogawa.core.data.anime.Anime
import id.anison.amanogawa.core.model.AnimeUI

fun Anime.toAnimeUI() = AnimeUI(
    animeId = animeId,
    name = name,
    isCurrent = isCurrent,
    enJpName = enJpName,
    dubedEpisodes = dubedEpisodes,
    episodesCustom = episodesCustom,
    episodesTotal = episodesTotal,
    extraTotal = extraTotal,
    ovaTotal = ovaTotal,
    year = year,
    season = season,
    part = part,
    views = views,
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

fun AnimeUI.toAnime() = Anime(
    animeId = animeId,
    name = name,
    isCurrent = isCurrent,
    enJpName = enJpName,
    dubedEpisodes = dubedEpisodes,
    episodesCustom = episodesCustom,
    episodesTotal = episodesTotal,
    extraTotal = extraTotal,
    ovaTotal = ovaTotal,
    year = year,
    season = season,
    part = part,
    views = views,
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