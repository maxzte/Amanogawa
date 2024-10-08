package id.anison.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite")
data class FavoriteDbo(
    @ColumnInfo val isFavorite: Boolean,
    @ColumnInfo val isCurrent: Boolean,
    @PrimaryKey val id: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val en_jap_name: String,
    @ColumnInfo val season: Int,
    @ColumnInfo val year: Int,
    @ColumnInfo val poster: String,
    @ColumnInfo val views: Int,
    @ColumnInfo val liked: Int = 0,
    @ColumnInfo val disliked: Int = 0,
    @ColumnInfo val timestamp: Long = 0,
    @ColumnInfo val episodes: Int = 0,
    @ColumnInfo val isNewEpisode: Boolean = false,
    @ColumnInfo val schedule: String = "",
)
