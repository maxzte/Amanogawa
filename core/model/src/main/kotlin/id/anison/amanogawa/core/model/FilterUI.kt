package id.anison.amanogawa.core.model

data class FilterUI(
    val filterTypeUI: FilterTypeUI = FilterTypeUI.SEARCH,
    var genres: List<GenreUI> = listOf(),
    val teamMembers: List<TeamMemberUI> = listOf(),
    val years: List<Short> = listOf(),
)

enum class FilterTypeUI {
    GENRE,
    DUBBER,
    TRANSLATOR,
    SOUND_ENGINEER,
    ALL_TEAM_MEMBER,
    YEAR,
    DISPLAYED,
    SEARCH
}

data class GenreUI(
    val genreId: Long,
    val name: String,
    val botIdName: String,
)

data class TeamMemberUI(
    val teamMemberId: Long,
    val name: String,
    val teamMemberType: TeamMemberTypeUI,
)

enum class TeamMemberTypeUI {
    VOICE,
    SOUND,
    TRANSLATOR,

}