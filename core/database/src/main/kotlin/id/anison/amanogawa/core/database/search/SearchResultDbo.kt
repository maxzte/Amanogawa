package id.anison.amanogawa.core.database.search

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_result")
data class SearchResultDbo(

    @PrimaryKey
    val animeId: Long,

    val name: String,
    val enJpName: String,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val posterMini: String,
    val timestamp: Long = System.currentTimeMillis(),

)