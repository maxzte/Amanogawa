package id.anison.amanogawa.core.data.search

import id.anison.amanogawa.core.network.model.anime.SearchResultDto
import id.anison.amanogawa.core.database.search.SearchResultDbo

data class SearchResult(
    val animeId: Long,
    val name: String,
    val enJpName: String,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val posterMini: String,
    val timestamp: Long = 0,
)

internal fun SearchResultDto.toSearchResult() = SearchResult(
    animeId = animeId,
    name = name,
    enJpName = enJpName,
    year = year,
    season = season,
    part = part,
    posterMini = posterMini,
)

internal fun SearchResultDbo.toSearchResult() = SearchResult(
    animeId = animeId,
    name = name,
    enJpName = enJpName,
    year = year,
    season = season,
    part = part,
    posterMini = posterMini,
    timestamp = timestamp,
)

internal fun SearchResult.toSearchResultDbo() = SearchResultDbo(
    animeId = animeId,
    name = name,
    enJpName = enJpName,
    year = year,
    season = season,
    part = part,
    posterMini = posterMini,
    timestamp = timestamp,
)
