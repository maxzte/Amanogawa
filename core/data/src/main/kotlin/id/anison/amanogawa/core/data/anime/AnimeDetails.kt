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
