package id.anison.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import id.anison.database.util.Converters
import kotlinx.serialization.Serializable

@TypeConverters(Converters::class)
@Entity(tableName="filter")
class FilterDbo (
    @PrimaryKey(autoGenerate = true) val id: Int,
    /*@Embedded("dubers")*/ val dubers :DubersDbo = DubersDbo(),
    /*@Embedded("genres") */val genres :List<GenreDbo> = listOf(),
    /*@Embedded("years")*/ val years :List<Int> = listOf(),
)

@Serializable
data class DubersDbo(
    val s: List<SDbo> = listOf(),
    val t: List<TDbo> = listOf(),
    val v: List<VDbo> = listOf(),
)

@Serializable
data class SDbo(
    val id: Int = 0,
    val name: String = "",
)

@Serializable
data class TDbo(
    val id: Int = 0,
    val name: String = "",
)

@Serializable
data class VDbo(
    val id: Int = 0,
    val name: String = "",
)

@Serializable
data class GenreDbo(
    val id: Int = 0,
    val name: String = "",
    val bot_id_name: String = "",
)

