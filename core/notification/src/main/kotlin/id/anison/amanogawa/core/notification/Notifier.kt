package id.anison.amanogawa.core.notification

import id.anison.amanogawa.core.data.anime.Anime

interface Notifier {

    fun postNewEpisodes(animeList: List<Anime>)

}