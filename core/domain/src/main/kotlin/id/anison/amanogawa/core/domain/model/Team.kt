package id.anison.amanogawa.core.domain.model

data class Team(
    val teamMemberId: Int = 0,
    val isCurrent: Boolean = false,
    val isOther: Boolean = false,
    val isSound: Boolean = false,
    val isTransl: Boolean = false,
    val isVoice: Boolean = false,
    val name: String = "",
    val roleText: String = "",
    val sound: String? = "",
    val titles: List<Int> = listOf(),
    val translator: String? = "",
    val userOrder: Int = 0,
    val voice: String? = "",
)
