package id.anison.database.anime.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import id.anison.database.filter.DubersDbo
import id.anison.database.genre.GenreDbo
import id.anison.database.util.Converters
import kotlin.math.round

@Entity(tableName = "anime_details")
@TypeConverters(Converters::class)
data class AnimeDetailsDbo(

    @PrimaryKey val animeId: Long,
    val duration: String = "",
    val company: String,
    val director: String,
    val description: String,
    val botId: Int,
    val liked: Int = 0,
    val disliked: Int = 0,
    val isMovie: Boolean = false,
    val isDubed: Boolean = false,
    val isHaveSubs: Boolean = false,
    val torrentUrl: String? = null,
    val torrent4kUrl: String? = null,
    val posterMaker: String = "",
    val poster: String = "",
    val botUrl: String,
    var rating: Int = if (liked + disliked > 0) {
        round(liked / (liked.toDouble() + disliked) * 5).toInt()
    } else {
        0
    },
    val screensF: List<List<String>> = emptyList(),
    val genres: List<GenreDbo>,
    val dubers: DubersDbo,
    val groupF: List<AnimeDbo>? = emptyList(),
)
