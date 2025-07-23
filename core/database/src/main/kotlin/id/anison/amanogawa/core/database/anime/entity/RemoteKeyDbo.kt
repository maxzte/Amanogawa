package id.anison.amanogawa.core.database.anime.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "remote_key")
data class RemoteKeyDbo(

    @PrimaryKey
    val key: String,
    val currentPage: Int,
    val nextPage: Int?,
    val lastUpdated: Long

)
