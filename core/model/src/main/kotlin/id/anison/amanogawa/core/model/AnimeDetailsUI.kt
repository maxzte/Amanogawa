package id.anison.amanogawa.core.model

data class AnimeDetailsUI(
    val animeUI: AnimeUI,
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
    val genres: List<GenreUI>,
    val teamMembers: List<TeamMemberUI>,
    val groupF: List<AnimeUI>? = emptyList(),
    val botUrl: String,
    val rating: Int,
) {
    override fun toString(): String {
        return "AnimeDetailsUI(\n" +
                "animeUI=$animeUI, \n" +
                "duration='$duration', \n" +
                "company='$company', \n" +
                "director='$director', \n" +
                "description='$description', \n" +
                "botId=$botId, \n" +
                "liked=$liked, \n" +
                "disliked=$disliked, \n" +
                "isMovie=$isMovie, \n" +
                "isDubbed=$isDubbed, \n" +
                "isHaveSubs=$isHaveSubs, \n" +
                "torrentUrl=$torrentUrl, \n" +
                "torrent4kUrl=$torrent4kUrl, \n" +
                "posterMaker='$posterMaker', \n" +
                "poster='$poster', \n" +
                "screensF=$screensF, \n" +
                "genres=$genres, \n" +
                "teamMembers=$teamMembers, \n" +
                "groupF=$groupF, \n" +
                "botUrl='$botUrl', \n" +
                "rating=$rating\n" +
                ")"
    }
}
