package id.anison.amanogawa.core.network.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.Transient


@Serializable
data class TeamMemberDto(

    @SerialName("id")
    val teamMemberId: Long,

    val name: String,

    @Transient
    val teamMemberType: TeamMemberTypeDto = TeamMemberTypeDto.VOICE,

)

enum class TeamMemberTypeDto {
    VOICE,
    SOUND,
    TRANSLATOR,
}

object TeamMembersSerializer : KSerializer<List<TeamMemberDto>> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(serialName = "dubers") {
        element(
            elementName = "t",
            descriptor = ListSerializer(elementSerializer = TeamMemberDto.serializer()).descriptor
        )
        element(
            elementName = "s",
            descriptor = ListSerializer(elementSerializer = TeamMemberDto.serializer()).descriptor
        )
        element(
            elementName = "v",
            descriptor = ListSerializer(elementSerializer = TeamMemberDto.serializer()).descriptor
        )
    }

    override fun deserialize(decoder: Decoder): List<TeamMemberDto> {
        val input = decoder as? JsonDecoder
            ?: throw SerializationException(message = "This serializer only works with Json")

        val jsonObj = input.decodeJsonElement().jsonObject
        val result = mutableListOf<TeamMemberDto>()

        jsonObj["t"]?.jsonArray?.forEach {
            val teamMember = input.json.decodeFromJsonElement(
                deserializer = TeamMemberDto.serializer(),
                element = it
            )
            result.add(
                TeamMemberDto(
                    teamMemberId = teamMember.teamMemberId,
                    name = teamMember.name,
                    teamMemberType = TeamMemberTypeDto.TRANSLATOR
                )
            )
        }
        jsonObj["s"]?.jsonArray?.forEach {
            val teamMember = input.json.decodeFromJsonElement(
                deserializer = TeamMemberDto.serializer(),
                element = it
            )
            result.add(
                TeamMemberDto(
                    teamMemberId = teamMember.teamMemberId,
                    name = teamMember.name,
                    teamMemberType = TeamMemberTypeDto.SOUND
                )
            )
        }
        jsonObj["v"]?.jsonArray?.forEach {
            val teamMember = input.json.decodeFromJsonElement(
                deserializer = TeamMemberDto.serializer(),
                element = it
            )
            result.add(
                TeamMemberDto(
                    teamMemberId = teamMember.teamMemberId,
                    name = teamMember.name,
                    teamMemberType = TeamMemberTypeDto.VOICE
                )
            )
        }

        return result
    }

    override fun serialize(
        encoder: Encoder,
        value: List<TeamMemberDto>
    ) {
        val output = encoder as? JsonEncoder
            ?: throw SerializationException(message = "This serializer only works with Json")

        val grouped = value.groupBy { it.teamMemberType }

        val json = buildJsonObject {
            put(
                key = "t",
                element = JsonArray(
                    content = grouped[TeamMemberTypeDto.TRANSLATOR].orEmpty().map {
                        output.json.encodeToJsonElement(
                            serializer = TeamMemberDto.serializer(),
                            value = TeamMemberDto(teamMemberId = it.teamMemberId, name = it.name)
                        )
                    }
                )
            )
            put(
                key = "s",
                element = JsonArray(
                    content = grouped[TeamMemberTypeDto.SOUND].orEmpty().map {
                        output.json.encodeToJsonElement(
                            serializer = TeamMemberDto.serializer(),
                            value = TeamMemberDto(teamMemberId = it.teamMemberId, name = it.name)
                        )
                    }
                )
            )
            put(
                key = "v",
                element = JsonArray(
                    content = grouped[TeamMemberTypeDto.VOICE].orEmpty().map {
                        output.json.encodeToJsonElement(
                            serializer = TeamMemberDto.serializer(),
                            value = TeamMemberDto(teamMemberId = it.teamMemberId, name = it.name)
                        )
                    }
                )
            )
        }

        output.encodeJsonElement(element = json)
    }

}

