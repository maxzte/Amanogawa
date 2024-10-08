package id.anison.api.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class Filter (
    @Transient val id: Int = 0,
    val dubers :Dubers = Dubers(),
    val genres :List<Genre> = listOf(),
    val years :List<Int> = listOf(),
)

@Serializable
data class Dubers(
    val s: List<S> = listOf(),
    val t: List<T> = listOf(),
    val v: List<V> = listOf()
)

@Serializable
data class S(
    val id: Int = 0,
    val name: String = ""
)

@Serializable
data class T(
    val id: Int = 0,
    val name: String = ""
)

@Serializable
data class V(
    val id: Int = 0,
    val name: String = ""
)

@Serializable
data class Genre(
    val bot_id_name: String = "",
    val id: Int = 0,
    val name: String = ""
)

