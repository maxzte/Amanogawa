package id.anison.amanogawa.core.database.anime.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "current_anime")
data class CurrentAnimeDbo(

    @PrimaryKey
    val currentAnimeId: Long,
    val name: String,
    val enJpName: String,
    val episodesCustom: String?,
    val episodesTotal: String?,
    val extraTotal: Int,
    val ovaTotal: Int,
    val episodeCount: String?,
    val newEpisode: Int = 0,

    val schedule: String?,
    val time: Double = 0.0,
    val timeStr: String = "",

    val isCurrent: Boolean,
    val isExtra: Boolean = false,
    val isOva: Boolean = false,

)
