package id.anison.amanogawa.core.notification

import id.anison.amanogawa.core.domain.model.Anime

interface Notifier {

    fun postNewEpisodes(animeList: List<Anime>)

}