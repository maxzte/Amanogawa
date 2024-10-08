package id.anison.feature.model

data class TeamItemUI(
    val id: Int = 0,
    val is_current: Boolean = false,
    val is_other: Boolean = false,
    val is_sound: Boolean = false,
    val is_transl: Boolean = false,
    val is_voice: Boolean = false,
    val name: String = "",
    val role_text: String = "",
    val sound: String? = "",
    val titles: List<Int> = listOf(),
    val translator: String? = "",
    val user_order: Int = 0,
    val voice: String? = "",
)
