package id.anison.amanogawa.core.data.anime

import id.anison.amanogawa.core.network.model.anime.AnimeDto
import id.anison.amanogawa.core.network.model.anime.AnimeDetailsDto
import id.anison.amanogawa.core.database.anime.entity.AnimeDbo
import id.anison.amanogawa.core.database.anime.entity.CurrentAnimeDbo


/**
 * Represent anime from main page where contains all titles.
 * [API](https://amanogawa.space/api/titles)
 */
data class Anime(
    val animeId: Long,
    val name: String,
    val isCurrent: Boolean,
    val enJpName: String,
    val dubedEpisodes: Int,
    val episodesCustom: String?,
    val episodesTotal: String?,
    val extraTotal: Int,
    val ovaTotal: Int,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val views: Int,
    val posterThumb: String,
    val posterMini: String,
    val schedule: String? = "",
    val episodeCount: String?,

    // current
    val newEpisode: Int = 0,
    val isExtra: Boolean = false,
    val isOva: Boolean = false,
    val time: Double = 0.0,
    val timeStr: String = "",
)

internal fun AnimeDto.toAnime() = Anime(
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

internal fun AnimeDbo.toAnime() = Anime(
    animeId = animeId,
    name = name,
    isCurrent = isCurrent,
    enJpName = enJpName,
    dubedEpisodes = dubbedEpisodes,
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

internal fun Anime.toAnimeDbo(listKey: String? = null) = AnimeDbo(
    animeId = animeId,
    name = name,
    isCurrent = isCurrent,
    enJpName = enJpName,
    dubbedEpisodes = dubedEpisodes,
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
    listKey = listKey,
)

internal fun AnimeDetailsDto.toAnime() = Anime(
    animeId = animeId,
    name = name,
    isCurrent = isCurrent,
    enJpName = enJpName,
    dubedEpisodes = dubbedEpisodes,
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
    schedule = null,
    episodeCount = episodeCount,
)

internal fun Anime.toCurrentAnimeDbo() = CurrentAnimeDbo(
    currentAnimeId = animeId,
    name = name,
    enJpName = enJpName,
    episodesCustom = episodesCustom,
    episodesTotal = episodesTotal,
    extraTotal = extraTotal,
    ovaTotal = ovaTotal,
    episodeCount = episodeCount,
    newEpisode = newEpisode,
    schedule = schedule,
    time = time,
    timeStr = timeStr,
    isCurrent = isCurrent,
    isExtra = isExtra,
    isOva = isOva,
)