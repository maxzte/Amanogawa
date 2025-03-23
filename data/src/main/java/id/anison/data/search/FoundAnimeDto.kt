package id.anison.data.search

import id.anison.api.model.anime.FoundAnime
import id.anison.database.foundanime.FoundAnimeDbo

data class FoundAnimeDto(
    val foundAnimeId: Long,
    val name: String,
    val enJpName: String,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val posterMini: String,
    val timestamp: Long = 0,
)

internal fun FoundAnime.toFoundAnimeDto() = FoundAnimeDto(
    foundAnimeId = foundAnimeId,
    name = name,
    enJpName = enJpName,
    year = year,
    season = season,
    part = part,
    posterMini = posterMini,
)

internal fun FoundAnimeDbo.toFoundAnimeDto() = FoundAnimeDto(
    foundAnimeId = foundAnimeId,
    name = name,
    enJpName = enJpName,
    year = year,
    season = season,
    part = part,
    posterMini = posterMini,
    timestamp = timestamp,
)

internal fun FoundAnimeDto.toFoundAnimeDbo() = FoundAnimeDbo(
    foundAnimeId = foundAnimeId,
    name = name,
    enJpName = enJpName,
    year = year,
    season = season,
    part = part,
    posterMini = posterMini,
    timestamp = timestamp,
)
