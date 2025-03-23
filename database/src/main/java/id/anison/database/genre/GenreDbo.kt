package id.anison.database.genre

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(tableName = "genre")
@Serializable
class GenreDbo(
    @PrimaryKey val genreId: Long,
    val name: String,
    val botIdName: String = "",
)