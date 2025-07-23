package id.anison.amanogawa.core.database.anime.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_ids")
class AnimeIdsDbo (

    @PrimaryKey
    val id: Long,

)