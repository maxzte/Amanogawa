package id.anison.data.model


data class DataFilter(
    val id: Int,
    val dubers: DataDubers = DataDubers(),
    val genres: List<DataGenre> = listOf(),
    val years: List<Int> = listOf(),
)

data class DataDubers(
    val s: List<DataS> = listOf(),
    val t: List<DataT> = listOf(),
    val v: List<DataV> = listOf(),
)

data class DataS(
    val id: Int,
    val name: String = "",
)

data class DataT(
    val id: Int,
    val name: String = "",
)

data class DataV(
    val id: Int,
    val name: String = "",
)

data class DataGenre(
    val id: Int,
    val name: String = "",
    val bot_id_name: String = "",
)