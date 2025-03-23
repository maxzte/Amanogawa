package id.anison.database.foundanime

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foundAnime")
class FoundAnimeDbo(

    @PrimaryKey
    val foundAnimeId: Long,

    val name: String,
    val enJpName: String,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val posterMini: String,
    val timestamp: Long = System.currentTimeMillis(),
)