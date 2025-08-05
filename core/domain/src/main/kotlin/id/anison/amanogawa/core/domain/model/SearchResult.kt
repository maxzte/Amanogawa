package id.anison.amanogawa.core.domain.model

data class SearchResult(
    val animeId: Long,
    val name: String,
    val enJpName: String,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val posterMini: String,
    val timestamp: Long = 0,
)
