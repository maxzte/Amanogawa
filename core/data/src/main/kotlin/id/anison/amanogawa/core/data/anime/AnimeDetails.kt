package id.anison.amanogawa.core.data.anime

import id.anison.amanogawa.core.data.filter.Genre
import id.anison.amanogawa.core.data.filter.TeamMember
import id.anison.amanogawa.core.data.filter.TeamMemberType
import id.anison.amanogawa.core.data.filter.toGenre
import id.anison.amanogawa.core.network.model.anime.AnimeDetailsDto


data class AnimeDetails(
    val anime: Anime,
    val duration: String = "",
    val company: String,
    val director: String,
    val description: String,
    val botId: Int,
    val liked: Int = 0,
    val disliked: Int = 0,
    val isMovie: Boolean = false,
    val isDubbed: Boolean = false,
    val isHaveSubs: Boolean = false,
    val torrentUrl: String? = null,
    val torrent4kUrl: String? = null,
    val posterMaker: String = "",
    val poster: String = "",
    val screensF: List<List<String>> = emptyList(),
    val genres: List<Genre>,
    val teamMembers: List<TeamMember>,
    val groupF: List<Anime>? = emptyList(),
    val botUrl: String,
    val rating: Int = 0,
)

internal fun AnimeDetails.isSame(other: AnimeDetails): Boolean {
    return this.anime.animeId == other.anime.animeId &&
            this.anime.name == other.anime.name &&
            this.anime.enJpName == other.anime.enJpName &&
            this.anime.season == other.anime.season &&
            this.anime.isCurrent == other.anime.isCurrent &&
            this.anime.year == other.anime.year &&
            this.anime.season == other.anime.season &&
            this.anime.part == other.anime.part &&
            this.anime.views == other.anime.views &&
            this.anime.isOva == other.anime.isOva &&
            this.anime.isExtra == other.anime.isExtra &&
            this.anime.ovaTotal == other.anime.ovaTotal &&
            this.anime.extraTotal == other.anime.extraTotal &&
            this.anime.dubedEpisodes == other.anime.dubedEpisodes &&
            this.anime.episodesCustom == other.anime.episodesCustom &&
            this.anime.dubedEpisodes == other.anime.dubedEpisodes &&
            this.anime.episodesTotal == other.anime.episodesTotal &&
            this.anime.posterThumb == other.anime.posterThumb &&
            this.anime.posterThumb == other.anime.posterThumb &&
            this.anime.posterMini == other.anime.posterMini &&
            this.anime.schedule == other.anime.schedule &&
            this.anime.episodeCount == other.anime.episodeCount &&
            this.anime.newEpisode == other.anime.newEpisode &&
            this.anime.time == other.anime.time &&
            this.anime.timeStr == other.anime.timeStr &&
            this.description == other.description &&
            this.rating == other.rating &&
            this.isMovie == other.isMovie &&
            this.duration == other.duration &&
            this.company == other.company &&
            this.botId == other.botId &&
            this.botUrl == other.botUrl &&
            this.liked == other.liked &&
            this.disliked == other.disliked &&
            this.isMovie == other.isMovie &&
            this.isDubbed == other.isDubbed &&
            this.isHaveSubs == other.isHaveSubs &&
            this.torrentUrl == other.torrentUrl &&
            this.torrentUrl == other.torrentUrl &&
            this.posterMaker == other.posterMaker &&
            this.poster == other.poster &&
            this.poster == other.poster &&
            this.screensF == other.screensF &&
            this.genres == other.genres &&
            this.teamMembers == other.teamMembers &&
            this.groupF == other.groupF
}

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
