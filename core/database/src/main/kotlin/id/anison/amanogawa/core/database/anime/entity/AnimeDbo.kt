package id.anison.amanogawa.core.database.anime.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(tableName = "anime")
@Serializable
data class AnimeDbo(

    @PrimaryKey
    val animeId: Long,
    val name: String,
    val isCurrent: Boolean,
    val enJpName: String,
    val dubbedEpisodes: Int,
    val episodesCustom: String?,
    val episodesTotal: String?,
    val extraTotal: Int,
    val ovaTotal: Int,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val views: Int,
    val posterThumb: String,
    val posterMini: String,
    val schedule: String?,
    val episodeCount: String?,
    // поля для поточних
    val newEpisode: Int = 0,
    val isExtra: Boolean = false,
    val isOva: Boolean = false,
    val time: Double = 0.0,
    val timeStr: String = "",

    val listKey: String? = null

)
