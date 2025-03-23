package id.anison.database.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite")
data class FavoriteDbo(

    @PrimaryKey()
    val favoriteId: Long,

    val isFavorite: Boolean,
    val isCurrent: Boolean,
    val name: String,
    val enJpName: String,
    val season: Byte,
    val part: Byte,
    val year: Short,
    val poster: String,
    val views: Int,
    val liked: Int = 0,
    val disliked: Int = 0,
    val timestamp: Long = 0,
    val episodes: Int = 0,
    val isNewEpisode: Boolean = false,
    val schedule: String = "",
)
