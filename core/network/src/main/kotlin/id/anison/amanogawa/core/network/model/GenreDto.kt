package id.anison.amanogawa.core.network.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

@Serializable
data class GenreDto(

    @SerialName("id")
    val genreId: Long,

    val name: String,

    @SerialName("bot_id_name")
    val botIdName: String? = "",

)

object GenresSerializer : KSerializer<List<GenreDto>> {

    override val descriptor: SerialDescriptor = ListSerializer(
        elementSerializer = GenreDto.serializer()
    ).descriptor

    override fun serialize(encoder: Encoder, value: List<GenreDto>) {
        val list = value.map { listOf(it.genreId.toString(), it.name) }
        encoder.encodeSerializableValue(
            serializer = ListSerializer(elementSerializer = ListSerializer(elementSerializer = String.serializer())),
            value = list
        )
    }

    override fun deserialize(decoder: Decoder): List<GenreDto> {
        val input = decoder as? JsonDecoder
            ?: throw SerializationException("This serializer only works with Json")

        val element = input.decodeJsonElement()

        if (element !is JsonArray) {
            throw SerializationException("Expected JsonArray for genres")
        }

        val isArrayOfObjects = element.firstOrNull()?.let { it is JsonObject } == true

        val result = if (isArrayOfObjects) {
            input.json.decodeFromJsonElement(
                deserializer = ListSerializer(elementSerializer = GenreDto.serializer()),
                element = element
            )
        } else {
            element.mapNotNull { arr ->
                val array = arr.jsonArray

                val id = array[0].jsonPrimitive.long
                val name = array[1].jsonPrimitive.content

                GenreDto(genreId = id, name = name)
            }
        }

        return result
    }

}
