package id.anison.database.anime.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "remote_key")
data class RemoteKeyDbo(
    @PrimaryKey val id: String,
    val totalPages: Int,
    val currentPage: Int,
    val nextPage: Int?,
    val lastUpdated: Long
)
