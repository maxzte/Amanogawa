package id.anison.api.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Genre(

    @SerialName("id")
    val genreId: Long,

    val name: String,

    @SerialName("bot_id_name")
    val botIdName: String = "",
)

object GenresSerializer : KSerializer<List<Genre>> {
    override val descriptor: SerialDescriptor = ListSerializer(Genre.serializer()).descriptor

    override fun serialize(encoder: Encoder, value: List<Genre>) {
        val list = value.map { listOf(it.genreId.toString(), it.name) }
        encoder.encodeSerializableValue(ListSerializer(ListSerializer(String.serializer())), list)
    }

    override fun deserialize(decoder: Decoder): List<Genre> {
        val list = decoder.decodeSerializableValue(
            ListSerializer(
                ListSerializer(
                    String.serializer()
                )
            )
        )
        return list.map { Genre(it[0].toLong(), it[1]) }
    }
}