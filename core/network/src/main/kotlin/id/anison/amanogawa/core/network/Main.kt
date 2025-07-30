package id.anison.amanogawa.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun main() {
    //https://amanogawa.space/api/filters
//    val r = getFilters()
//    println(r)
//    val a = getAnime()
//    println(a)
//    val f = getFAnime()
//    println(f)

    val e = getEp()
    println(e)
}

private suspend fun getFilters() = withContext(Dispatchers.IO) {
    return@withContext AmanogawaApiImpl().getFilter()
}

private suspend fun getAnime() = withContext(Dispatchers.IO) {
    return@withContext AmanogawaApiImpl().getAnime(138)
}

private suspend fun getFAnime() = withContext(Dispatchers.IO) {
    return@withContext AmanogawaApiImpl().getFilteredTitles(
        1,
        null,
        null,
        listOf(2),
        sounds = listOf(2),
        translators = null
    )
}

private suspend fun getEp() = withContext(Dispatchers.IO) {
    return@withContext AmanogawaApiImpl().getAnimeEpisodes(116L)
}