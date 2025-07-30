package id.anison.amanogawa.core.model


data class SearchResultUI(
    val animeId: Long,
    val name: String,
    val enJpName: String,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val posterMini: String,
    val timestamp: Long,
)
