package id.anison.amanogawa.core.data.search

import id.anison.amanogawa.core.database.search.SearchResultDbo
import id.anison.amanogawa.core.domain.model.SearchResult
import id.anison.amanogawa.core.network.model.anime.SearchResultDto

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