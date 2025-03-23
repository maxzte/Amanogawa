package id.anison.api.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long


@Serializable
data class Filter (

    @Transient
    val id: Int = 0,

    @Serializable(with = DubersSerializer::class)
    val dubers :Dubers = Dubers(),

    val genres :List<Genre> = listOf(),

    val years :List<Short> = listOf(),
)

object DubersSerializer : KSerializer<Dubers> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("id.anison.api.model.Dubers.Filter")

    override fun serialize(encoder: Encoder, value: Dubers) {
        val jsonEncoder = encoder as JsonEncoder
        val jsonObject = buildJsonObject {
            put("s", buildJsonObject { value.s.forEach { put(it.key.toString(), JsonPrimitive(it.value)) } })
            put("t", buildJsonObject { value.t.forEach { put(it.key.toString(), JsonPrimitive(it.value)) } })
            put("v", buildJsonObject { value.v.forEach { put(it.key.toString(), JsonPrimitive(it.value)) } })
        }
        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): Dubers {
        val jsonDecoder = decoder as JsonDecoder
        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        fun parseCategory(category: String): Map<Long, String> {
            return jsonObject[category]?.jsonArray?.associate {
                val obj = it.jsonObject
                obj["id"]!!.jsonPrimitive.long to obj["name"]!!.jsonPrimitive.content
            } ?: emptyMap()
        }

        return Dubers(
            s = parseCategory("s"),
            t = parseCategory("t"),
            v = parseCategory("v"),
        )
    }
}
