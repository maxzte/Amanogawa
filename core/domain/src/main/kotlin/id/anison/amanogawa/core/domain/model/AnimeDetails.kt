package id.anison.amanogawa.core.domain.model



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
    val rating: Int,
)
