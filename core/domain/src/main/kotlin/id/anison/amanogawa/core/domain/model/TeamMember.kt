package id.anison.amanogawa.core.domain.model


data class TeamMember(
    val teamMemberId: Long,
    val name: String = "",
    val teamMemberType: TeamMemberType,
)

enum class TeamMemberType {
    VOICE,
    SOUND,
    TRANSLATOR,
}
