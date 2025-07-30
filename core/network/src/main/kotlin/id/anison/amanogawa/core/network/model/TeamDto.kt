package id.anison.amanogawa.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(

    @SerialName("id")
    val teamMemberId: Int,

    @SerialName("is_current")
    val isCurrent: Boolean = false,

    @SerialName("is_other")
    val isOther: Boolean = false,

    @SerialName("is_sound")
    val isSound: Boolean = false,

    @SerialName("is_transl")
    val isTransl: Boolean = false,

    @SerialName("is_voice")
    val isVoice: Boolean = false,

    val name: String = "",

    @SerialName("role_text")
    val roleText: String = "",

    val sound: String? = "",

    val titles: List<Int> = listOf(),

    val translator: String? = "",

    @SerialName("user_order")
    val userOrder: Int = 0,

    val voice: String? = "",

)