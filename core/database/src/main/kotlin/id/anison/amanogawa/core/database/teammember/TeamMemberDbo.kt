package id.anison.amanogawa.core.database.teammember

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Entity(
    tableName = "team_member",
    indices = [Index(value = ["name"], unique = true)],
)
data class TeamMemberDbo(
    @PrimaryKey(autoGenerate = true)
    val teamMemberId: Long = 0L,
    val name: String,
)

@Entity(
    tableName = "dubber",
    foreignKeys = [
        ForeignKey(
            entity = TeamMemberDbo::class,
            parentColumns = ["teamMemberId"],
            childColumns = ["teamMemberId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("teamMemberId")],
)
@Serializable
data class DubberDbo(
    @PrimaryKey
    val dubberId: Long,
    val teamMemberId: Long,
)

@Entity(
    tableName = "translator",
    foreignKeys = [
        ForeignKey(
            entity = TeamMemberDbo::class,
            parentColumns = ["teamMemberId"],
            childColumns = ["teamMemberId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("teamMemberId")],
)
data class TranslatorDbo(
    @PrimaryKey
    val translatorId: Long,
    val teamMemberId: Long,
)

@Entity(
    tableName = "sound_engineer",
    foreignKeys = [
        ForeignKey(
            entity = TeamMemberDbo::class,
            parentColumns = ["teamMemberId"],
            childColumns = ["teamMemberId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("teamMemberId")],
)
data class SoundEngineerDbo(
    @PrimaryKey
    val soundEngineerId: Long,
    val teamMemberId: Long,
)

data class TeamMemberWithRoles(
    @Embedded
    val member: TeamMemberDbo,

    @Relation(
        parentColumn = "teamMemberId",
        entityColumn = "teamMemberId",
    )
    val dubbers: List<DubberDbo> = emptyList(),

    @Relation(
        parentColumn = "teamMemberId",
        entityColumn = "teamMemberId",
    )
    val translators: List<TranslatorDbo> = emptyList(),

    @Relation(
        parentColumn = "teamMemberId",
        entityColumn = "teamMemberId",
    )
    val soundEngineers: List<SoundEngineerDbo> = emptyList()
)

enum class TeamMemberTypeDbo {
    VOICE,
    SOUND,
    TRANSLATOR,
}