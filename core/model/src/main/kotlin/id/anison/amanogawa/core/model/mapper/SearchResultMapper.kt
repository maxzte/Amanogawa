package id.anison.amanogawa.core.model.mapper

import id.anison.amanogawa.core.domain.model.SearchResult
import id.anison.amanogawa.core.model.SearchResultUI

fun SearchResult.toSearchResultUI() = SearchResultUI(
    animeId = animeId,
    name = name,
    enJpName = enJpName,
    year = year,
    season = season,
    part = part,
    posterMini = posterMini,
    timestamp = timestamp,
)

fun SearchResultUI.toSearchResult() = SearchResult(
    animeId = animeId,
    name = name,
    enJpName = enJpName,
    year = year,
    season = season,
    part = part,
    posterMini = posterMini,
    timestamp = System.currentTimeMillis(),
)