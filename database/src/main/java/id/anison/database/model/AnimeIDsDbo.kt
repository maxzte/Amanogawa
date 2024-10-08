package id.anison.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animeIDs")
class AnimeIDsDbo (
    @PrimaryKey val id: Long,
    //@ColumnInfo(name = "maxTitles") val maxTitles: Int
)