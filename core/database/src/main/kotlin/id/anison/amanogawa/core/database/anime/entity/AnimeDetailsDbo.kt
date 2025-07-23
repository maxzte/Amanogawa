package id.anison.amanogawa.core.database.anime.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import id.anison.amanogawa.core.database.teammember.DubberDbo
import id.anison.amanogawa.core.database.genre.GenreDbo
import id.anison.amanogawa.core.database.teammember.TeamMemberWithRoles
import kotlin.math.round

@Entity(tableName = "anime_details")
data class AnimeDetailsDbo(

    @PrimaryKey
    val animeId: Long,
    val duration: String = "",
    val company: String,
    val director: String,
    val description: String,
    val botId: Int,
    val liked: Int = 0,
    val disliked: Int = 0,
    val isMovie: Boolean = false,
    val isDubbed: Boolean = false,
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

//    val teamMembers: List<TeamMemberWithRoles>,
    val groupF: List<AnimeDbo>? = emptyList(),

)
