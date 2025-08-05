package id.anison.amanogawa.core.data.anime

import id.anison.amanogawa.core.data.filter.toGenre
import id.anison.amanogawa.core.database.anime.entity.AnimeDbo
import id.anison.amanogawa.core.database.anime.entity.CurrentAnimeDbo
import id.anison.amanogawa.core.domain.model.Anime
import id.anison.amanogawa.core.domain.model.AnimeDetails
import id.anison.amanogawa.core.domain.model.AnimeEpisode
import id.anison.amanogawa.core.domain.model.AnimeEpisodes
import id.anison.amanogawa.core.domain.model.TeamMember
import id.anison.amanogawa.core.domain.model.TeamMemberType
import id.anison.amanogawa.core.network.model.anime.AnimeDetailsDto
import id.anison.amanogawa.core.network.model.anime.AnimeDto
import id.anison.amanogawa.core.network.model.anime.AnimeEpisodeDto
import id.anison.amanogawa.core.network.model.anime.AnimeEpisodesDto

internal fun AnimeDto.toAnime() = Anime(
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
)

internal fun AnimeDbo.toAnime() = Anime(
    animeId = animeId,
    name = name,
    isCurrent = isCurrent,
    enJpName = enJpName,
    dubbedEpisodes = dubbedEpisodes,
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
    dubbedEpisodes = dubbedEpisodes,
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

internal fun AnimeDetailsDto.toAnimeDetails(): AnimeDetails {
    val anime = this.toAnime()
    val dubers = mutableListOf<TeamMember>()
    val s = soundsF.map { strings ->
        TeamMember(teamMemberId = strings[0].toLong(), name = strings[1], teamMemberType = TeamMemberType.SOUND)
    }
    dubers.addAll(s)
    val v = voicesF.map { strings ->
        TeamMember(teamMemberId = strings[0].toLong(), name = strings[1], teamMemberType = TeamMemberType.VOICE)
    }
    dubers.addAll(v)
    val t = translF.map { strings ->
        TeamMember(teamMemberId = strings[0].toLong(), name = strings[1], teamMemberType = TeamMemberType.TRANSLATOR)
    }
    dubers.addAll(t)
    return AnimeDetails(
        anime = anime,
        duration = duration.toString(),
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
        posterMaker = posterMaker.toString(),
        poster = poster.toString(),
        screensF = screensF,
        genres = genres.map { it.toGenre() },
        teamMembers = dubers,
        groupF = groupF?.map { it.toAnime() },
        botUrl = botUrl,
    )
}

internal fun AnimeDetailsDto.toAnime() = Anime(
    animeId = animeId,
    name = name,
    isCurrent = isCurrent,
    enJpName = enJpName,
    dubbedEpisodes = dubbedEpisodes,
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

internal fun AnimeEpisodesDto.toAnimeEpisodes() = AnimeEpisodes(
    botUrl = botUrl,
    data = data.map { it.toAnimeEpisode() },
    pages = pages,
)

internal fun AnimeEpisodeDto.toAnimeEpisode() = AnimeEpisode(
    episodeId = episodeId,
    animeId = animeId,
    botId = botId,
    isExtra = isExtra,
    isOva = isOva,
    name = name,
    number = number,
    screen = screen,
    screenOriginal = screenOriginal,
)
