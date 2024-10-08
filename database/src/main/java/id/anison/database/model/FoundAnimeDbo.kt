package id.anison.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foundAnime")
class FoundAnimeDbo(
    @PrimaryKey val id: Long,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("en_jp_name") val en_jp_name: String,
    @ColumnInfo("year") val year: Short,
    @ColumnInfo("season") val season: Byte,
    @ColumnInfo("part") val part: Byte,
    @ColumnInfo("poster_mini") val poster_mini: String,
    @ColumnInfo("timestamp") val timestamp: Long,
)