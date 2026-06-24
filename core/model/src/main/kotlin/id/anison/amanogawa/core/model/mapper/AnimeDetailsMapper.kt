package id.anison.amanogawa.core.model.mapper

import id.anison.amanogawa.core.domain.model.AnimeDetails
import id.anison.amanogawa.core.model.AnimeDetailsUI

fun AnimeDetails.toAnimeDetailsUI() = AnimeDetailsUI(
    animeUI = anime.toAnimeUI(),
    duration = Regex("\\d{1,2}:\\d{2}:\\d{2}|\\d+").find(duration)?.value.orEmpty(),
    company = company,
    director = director,
    description = description,
    botId = botId,
    liked = liked,
    disliked = disliked,
    isMovie = isMovie,
    isDubbed = isDubbed,
    isHaveSubs = isHaveSubs,
    torrentUrl = torrentUrl,
    torrent4kUrl = torrent4kUrl,
    posterMaker = posterMaker,
    poster = poster,
    screensF = screensF,
    genres = genres.map { it.toGenreUI() },
    teamMembers = teamMembers.map { it.toTeamMemberUI() },
    groupF = groupF?.map { it.toAnimeUI() },
    botUrl = botUrl,
    rating = rating,
)
