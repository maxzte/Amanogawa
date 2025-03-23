package id.anison.data.anime

import id.anison.api.model.anime.Anime
import id.anison.api.model.anime.AnimeDetails
import id.anison.database.anime.entity.AnimeDbo


/**
 * Represent anime from main page where contains all titles.
 * [API](https://amanogawa.space/api/titles)
 */
data class AnimeDto(
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

internal fun Anime.toAnimeDto() = AnimeDto(
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

internal fun AnimeDbo.toAnimeDto() = AnimeDto(
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

internal fun AnimeDto.toAnimeDbo() = AnimeDbo(
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

internal fun AnimeDetails.toAnimeDto() = AnimeDto(
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
    schedule = null,
    episodeCount = episodeCount,

)