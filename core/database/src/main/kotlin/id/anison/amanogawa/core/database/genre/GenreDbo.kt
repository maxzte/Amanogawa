package id.anison.amanogawa.core.database.genre

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "genre")
data class GenreDbo(

    @PrimaryKey
    val genreId: Long,

    val name: String,

    val botIdName: String = "",

)
