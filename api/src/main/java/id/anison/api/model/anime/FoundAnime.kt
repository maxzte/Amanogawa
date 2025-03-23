package id.anison.api.model.anime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoundAnime(

    @SerialName("id")
    val foundAnimeId: Long,

    val name: String,

    @SerialName("en_jp_name")
    val enJpName: String,

    val year: Short,

    val season: Byte,

    val part: Byte,

    @SerialName("poster_mini")
    val posterMini: String,

)
