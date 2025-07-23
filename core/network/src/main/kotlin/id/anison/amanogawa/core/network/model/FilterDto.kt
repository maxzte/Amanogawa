package id.anison.amanogawa.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FilterDto (

    @Serializable(with = TeamMembersSerializer::class)
    @SerialName(value = "dubers")
    val teamMembers :List<TeamMemberDto> = listOf(),

    val genres :List<GenreDto> = listOf(),

    val years :List<Short> = listOf(),

)
