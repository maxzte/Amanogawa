package id.anison.feature.search.model

import id.anison.data.search.FoundAnimeDto


data class FoundAnimeUI(
    val foundAnimeId: Long,
    val name: String,
    val enJpName: String,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val posterMini: String,
    val timestamp: Long,
)

internal fun FoundAnimeDto.toFoundAnimeUI() = FoundAnimeUI(
    foundAnimeId = foundAnimeId,
    name = name,
    enJpName = enJpName,
    year = year,
    season = season,
    part = part,
    posterMini = posterMini,
    timestamp = timestamp,
)

internal fun FoundAnimeUI.toDataFoundAnime() = FoundAnimeDto(
    foundAnimeId = foundAnimeId,
    name = name,
    enJpName = enJpName,
    year = year,
    season = season,
    part = part,
    posterMini = posterMini,
    timestamp = timestamp,
)
