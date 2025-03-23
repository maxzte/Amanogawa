import id.anison.api.AmanogawaApiImpl


suspend fun main() {
//     val api = AmanogawaApiImpl()
    getEps()
}

suspend fun getEps() {
    val resp = AmanogawaApiImpl().getAnimeEpisodes(127L)
    if (resp.isFailure) {
        println("FAIL")
    } else {
        resp.getOrThrow().data.forEach{ println(it.number)}
    }
}

class AmanogawaApiTest {

}