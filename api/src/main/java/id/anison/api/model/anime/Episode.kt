package id.anison.api.model.anime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Episode(

    @SerialName("bot_id")
    val botId: Long = 0,

    val episodeId: Long = 0,

    @SerialName("is_extra")
    val isExtra: Boolean = false,

    @SerialName("is_ova")
    val isOva: Boolean = false,

    val name: String = "",

    val number: Int = 0,

    @SerialName("post_date")
    val postDate: String = "",

    @SerialName("post_update")
    val postUpdate: String = "",

    val screen: String = "",

    @SerialName("screen_original")
    val screenOriginal: String = "",

    @SerialName("title_id")
    val animeId: Int = 0
)

@Serializable
data class Episodes(

    val pages: Int = 0,

    val data: List<Episode>,

    @SerialName("bot_url")
    val botUrl: String = "",
)