package id.anison.database.filter

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import id.anison.database.genre.GenreDbo
import id.anison.database.util.Converters
import kotlinx.serialization.Serializable

@TypeConverters(Converters::class)
@Entity(tableName = "filter")
class FilterDbo(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val dubers: DubersDbo = DubersDbo(),
    val genres: List<GenreDbo> = listOf(),
    val years: List<Short> = listOf(),
)

@Serializable
data class DubersDbo(
    val s: Map<Long, String> = emptyMap(),
    val t: Map<Long, String> = emptyMap(),
    val v: Map<Long, String> = emptyMap(),
)



