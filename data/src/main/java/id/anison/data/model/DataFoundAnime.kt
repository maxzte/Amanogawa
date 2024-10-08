package id.anison.data.model

data class DataFoundAnime(
    val id: Long,
    val name: String,
    val en_jp_name: String,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val poster_mini: String,
    val timestamp: Long,
)
