package id.anison.feature.model

data class FilterUI(
    val id: Int = 0,
    val dubers: DubersUI = DubersUI(),
    var genres: List<GenreUI> = listOf(),
    val years: List<Int> = listOf(),
)

data class DubersUI(
    val s: List<SUI> = listOf(),
    val t: List<TUI> = listOf(),
    val v: List<VUI> = listOf(),
)

data class SUI(
    val id: Int,
    val name: String = "",
)

data class TUI(
    val id: Int,
    val name: String = "",
)

data class VUI(
    val id: Int,
    val name: String = "",
)

data class GenreUI(
    val id: Int,
    val name: String = "",
    val bot_id_name: String = "",
)
