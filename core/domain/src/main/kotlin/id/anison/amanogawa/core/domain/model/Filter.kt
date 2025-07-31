package id.anison.amanogawa.core.domain.model


data class Filter(
    val filterType: FilterType = FilterType.DISPLAYED,
    val genres: List<Genre> = listOf(),
    val teamMembers: List<TeamMember> = listOf(),
    val years: List<Short> = listOf(),
)

enum class FilterType {
    GENRE,
    DUBBER,
    TRANSLATOR,
    SOUND_ENGINEER,
    ALL_TEAM_MEMBER,
    YEAR,
    DISPLAYED,
    SEARCH
}
